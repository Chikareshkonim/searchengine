package in.nimbo.moama.util;
public enum PropertyType implements in.nimbo.moama.configmanager.PropertyType {
    H_BASE_CONTENT_FAMILY("hbase.content.family"), H_BASE_TABLE("hbase.table");

    public void setType(String type) {


    }

    private String type;
    PropertyType(String type) {
        this.type = type;
    }
    public String toString() {
        return type;
    }
}
