public class Courier {
    private String login;
    private String password;
    private String firstName;

    //делаем обязательный конструктор по умолчанию, чтобы его использовала GSON
    public Courier() {
    }

    //делаем конструктор с параметрами для удобства
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    //генерируем геттеры и сеттеры
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
