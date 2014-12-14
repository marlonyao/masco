namespace java banner
namespace php Banner

struct Location {
    //1:string module;
    2:string page;
    3:string area;
}

struct Banner {
    1:i32 id;
    2:i32 cityId;
    3:Location location;
    4:string imageUrl;
    5:string url;
}

service BannerService {
    i32 createBanner(1:Banner banner);
    void updateBanner(1:Banner banner);
    list<Banner> getBanners(1:i32 cityId);
}
