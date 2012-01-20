package org.filippodeluca.todo.client.domain;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * @author Filippo De Luca
 */
@RemoteServiceRelativePath("todo")
public interface TodoService extends RemoteService {

    ArrayList<Todo> listAll();

    Todo save(Todo toSave);

    Todo add(Todo toAdd);

}
