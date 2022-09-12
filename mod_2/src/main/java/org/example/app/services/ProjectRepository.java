package org.example.app.services;

import org.example.web.dto.BookToRemove;

import java.util.List;

public interface ProjectRepository<T> {

    List<T> retrieveAll();

    void store(T book);

    boolean removeItem(BookToRemove bookIdToRemove);
}
