package com.example.dcardtry;

public class GetAccountInfo {
    private String name;
    private String jobTitle;
    private String mail;
    private String password;
    private String administrator;

    public GetAccountInfo(){
        this.name=name;
        this.jobTitle=jobTitle;
        this.mail=mail;
        this.password=password;
        this.administrator=administrator;
    }

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getjobTitle(){return jobTitle;}

    public void setjobTitle(String jobTitle){this.jobTitle=jobTitle;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password=password;}

    public String getMail(){return mail;}

    public void setMail(String mail){this.mail=mail;}

    public String getAdministrator(){return administrator;}

    public void setAdministrator(String administrator){this.administrator=administrator;}


}