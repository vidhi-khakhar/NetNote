package server.api;

import commons.CollectionContainsNotes;
import commons.CollectionContainsNotesId;
import server.database.CollectionContainsNotesRepository;

import java.util.*;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestCollectionContainsNotesRepository implements CollectionContainsNotesRepository {

    public final Map<CollectionContainsNotesId, CollectionContainsNotes> relationships = new HashMap<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<CollectionContainsNotes> findAll() {
        call("findAll");
        return new ArrayList<>(relationships.values());
    }

    @Override
    public List<CollectionContainsNotes> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CollectionContainsNotes> findAllById(Iterable<CollectionContainsNotesId> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends CollectionContainsNotes> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<CollectionContainsNotes> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<CollectionContainsNotesId> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub
    }

    @Override
    public CollectionContainsNotes getOne(CollectionContainsNotesId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CollectionContainsNotes getById(CollectionContainsNotesId id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public CollectionContainsNotes getReferenceById(CollectionContainsNotesId id) {
        call("getReferenceById");
        return find(id).get();
    }

    private Optional<CollectionContainsNotes> find(CollectionContainsNotesId id) {
        CollectionContainsNotes relationship = relationships.get(id);
        if (relationship == null) return Optional.empty();
        return Optional.of(relationship);
    }

    @Override
    public <S extends CollectionContainsNotes> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<CollectionContainsNotes> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> S save(S entity) {
        call("save");
        relationships.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<CollectionContainsNotes> findById(CollectionContainsNotesId id) {
        call("find");
        return find(id);
    }

    @Override
    public boolean existsById(CollectionContainsNotesId id) {
        call("existsById");
        return relationships.containsKey(id);
    }

    @Override
    public long count() {
        return relationships.size();
    }

    @Override
    public void deleteById(CollectionContainsNotesId id) {
        call("deleteById");
        relationships.remove(id);
    }

    @Override
    public void delete(CollectionContainsNotes entity) {
        call("delete");
        relationships.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends CollectionContainsNotesId> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends CollectionContainsNotes> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        call("deleteAll");
        relationships.clear();
    }

    @Override
    public <S extends CollectionContainsNotes> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends CollectionContainsNotes> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends CollectionContainsNotes> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends CollectionContainsNotes, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
