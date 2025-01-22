package server.api;

import commons.Note;
import server.database.NoteRepository;

import java.util.*;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestNoteRepository implements NoteRepository {

    public final Map<Long, Note> notes = new HashMap<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Note> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(notes.values());
    }

    @Override
    public List<Note> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Note> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Note> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Note> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public Note getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Note getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Note getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    private Optional<Note> find(Long id) {
        Note note = notes.get(id);
        if (note == null) return Optional.empty();
        return Optional.of(note);
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Note> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> S save(S entity) {
        call("save");
        notes.put((long) notes.size(), entity);
        return entity;
    }

    @Override
    public Optional<Note> findById(Long id) {
        call("find");
        return find(id);
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return notes.size();
    }

    @Override
    public void deleteById(Long id) {
        notes.remove(id);
    }

    @Override
    public void delete(Note entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends Note> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Note> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Note> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends Note> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Note, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Note> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public List<Note> findByTextContaining(String text) {
        return List.of();
    }
}
