package tales.model;

public class Account {

    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Account() {
    }

    public Account(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}