package com.meituan.masco.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TIOStreamTransport;

public class MascoServlet<I> extends HttpServlet {

	// private final TProcessor processor;
	// private InvocationController<I> controller;

	private I handler;
	private Class<I> handlerType;
	private List<InvokeFilter> filters;
	// private Class processorType;
	private ProcessorFactory<I> processorFactory;

	// private final TProtocolFactory inProtocolFactory;

	// private final TProtocolFactory outProtocolFactory;

	private final Collection<Map.Entry<String, String>> customHeaders;

	public MascoServlet(ProcessorFactory<I> processorFactory, I handler, Class<I> handlerType) {
		super();
		this.processorFactory = processorFactory;
		this.handler = handler;
		// TODO: use reflection to get handlerType
		this.handlerType = handlerType;
		this.filters = new ArrayList<InvokeFilter>();
		this.customHeaders = new ArrayList<Map.Entry<String, String>>();
	}

	private TProcessor createProcessor(I handler) {
		return processorFactory.createProcessor(handler);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			response.setContentType("application/x-thrift");

			if (null != this.customHeaders) {
				for (Map.Entry<String, String> header : this.customHeaders) {
					response.addHeader(header.getKey(), header.getValue());
				}
			}
			InputStream in = request.getInputStream();
			OutputStream out = response.getOutputStream();

			InvocationController<I> controller = new InvocationController<I>(handler, handlerType);
			for (InvokeFilter filter : filters) {
				controller.addFilter(filter);
			}
			String uri = request.getScheme()
					+ "://"
					+ request.getServerName()
					+ ("http".equals(request.getScheme())
							&& request.getServerPort() == 80
							|| "https".equals(request.getScheme())
							&& request.getServerPort() == 443 ? "" : ":"
							+ request.getServerPort())
					+ request.getRequestURI()
					+ (request.getQueryString() != null ? "?"
							+ request.getQueryString() : "");
			controller.setMetadata(InvocationController.KEY_URI, uri);

			for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames
					.hasMoreElements();) {
				String headerName = headerNames.nextElement();
				String headerValue = request.getHeader(headerName);
				controller.setMetadata(headerName, headerValue);
			}

			TProcessor processor = createProcessor(controller.createProxy());
			MascoTransport transport = new MascoTransport(
					new TIOStreamTransport(in, out));
			MascoProtocol protocol = new MascoProtocol(transport);
			processor.process(protocol, protocol);
			out.flush();
		} catch (TException te) {
			throw new ServletException(te);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void addFilter(InvokeFilter filter) {
		this.filters.add(filter);
	}

	public void addCustomHeader(final String key, final String value) {
		this.customHeaders.add(new Map.Entry<String, String>() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public String getValue() {
				return value;
			}

			@Override
			public String setValue(String value) {
				return null;
			}
		});
	}

	public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
		this.customHeaders.clear();
		this.customHeaders.addAll(headers);
	}
}
