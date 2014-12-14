namespace java hello
namespace php Hello

struct Person {
    1:string firstName;
    2:string lastName;
}

service HelloService {
    void ping();
    string hello(1:string name);
    string helloV2(1:Person person);
}
