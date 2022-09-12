package org.example.app.services;

import java.util.List;

public interface ProjectRepository<T> {

    List<T> retrieveAll();

    void store(T book);

    void removeItemById(Integer bookIdToRemove);

    void removeItem(String author, String title, Integer size);
}
