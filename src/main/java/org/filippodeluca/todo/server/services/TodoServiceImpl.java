package org.filippodeluca.todo.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.filippodeluca.todo.client.domain.GreetingService;
import org.filippodeluca.todo.client.domain.Todo;
import org.filippodeluca.todo.client.domain.TodoService;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Lists.*;

/**
 * @author Filippo De Luca
 */
public class TodoServiceImpl extends RemoteServiceServlet implements TodoService {

    private Map<String, Todo> todos = new ConcurrentHashMap<String, Todo>();

    public TodoServiceImpl() {
    }

    public TodoServiceImpl(Object delegate) {
        super(delegate);
    }

    public ArrayList<Todo> listAll() {
        return newArrayList(todos.values());
    }

    public Todo save(Todo toSave) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Todo add(Todo toAdd) {
        todos.put(toAdd.getName(), toAdd);
        return toAdd;
    }
}
