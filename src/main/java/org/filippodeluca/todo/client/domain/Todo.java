package org.filippodeluca.todo.client.domain;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Filippo De Luca
 */
public class Todo implements IsSerializable {

    private Long id;
    private String name;
    private Boolean done;

    public Todo(Long id, String name, Boolean done) {
        this.id = id;
        this.done = done;
        this.name = name;
    }

    public Todo(String name, Boolean done) {
        this(null, name, done);
    }

    public Todo(String name) {
        this(null, name, false);
    }

    public Todo() {
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
