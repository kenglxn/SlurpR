package net.glxn.slurpr.model;

public class Person {
    private String id;
    private String name;
    private String age;
    private Person relation;

    private Person() {}

    public Person(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public Person getRelation() {
        return relation;
    }
}
