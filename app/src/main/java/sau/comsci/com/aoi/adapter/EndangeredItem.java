package sau.comsci.com.aoi.adapter;


public class EndangeredItem {
    private String title,detail,thumbnail;

    public EndangeredItem()
    {}

    public EndangeredItem(String title, String detail,String thumbnail)
    {
        this.title = title;
        this.detail = detail;
        this.thumbnail = thumbnail;
    }
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
