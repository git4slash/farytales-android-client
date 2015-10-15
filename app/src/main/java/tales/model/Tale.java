package tales.model;

import java.io.Serializable;

public class Tale implements Serializable {

    private Long id;

    public String uri;
    public String name;
    public String text;

    public Tale() {}

    public Tale(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Tale(Long id, String uri, String name, String text) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.text = text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Tale{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}