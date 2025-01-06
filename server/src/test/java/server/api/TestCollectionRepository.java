package server.api;

import commons.Collection;
import server.database.CollectionRepository;

import java.util.*;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestCollectionRepository implements CollectionRepository {

    public final Map<String, Collection> Collections = new HashMap<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Collection> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(Collections.values());
    }

    @Override
    public List<Collection> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Collection> findAllById(Iterable<String> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Collection> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Collection> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection getOne(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection getById(String id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Collection getReferenceById(String id) {
        call("getReferenceById");
        return find(id).get();
    }

    private Optional<Collection> find(String id) {
        Collection Collection = Collections.get(id);
        if (Collection == null) return Optional.empty();
        return Optional.of(Collection);
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Collection> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> S save(S entity) {
        call("save");
        Collections.put(entity.getCollectionUniqueName(), entity);
        return entity;
    }

    @Override
    public Optional<Collection> findById(String id) {
        call("find");
        return find(id);
    }

    @Override
    public boolean existsById(String id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return Collections.size();
    }

    @Override
    public void deleteById(String id) {
        Collections.remove(id);
    }

    @Override
    public void delete(Collection entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends Collection> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Collection> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Collection> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends Collection> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Collection, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
