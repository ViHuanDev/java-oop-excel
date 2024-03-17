package huanvc.example;

public class ExcelModel {
    private String id;

    private String start_type;
    private String count;
    private String proportion;
    private Integer row_index;

    public String getStart_type() {
        return start_type;
    }

    public void setStart_type(String start_type) {
        this.start_type = start_type;
    }

    public String getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public int getRow_index() {
        return row_index;
    }

    public void setRow_index(Integer row_index) {
        this.row_index = row_index;
    }
}
