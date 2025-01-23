package server.api;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.EmbeddedFileRepository;

import java.util.*;
import java.util.function.Function;

public class TestEmbeddedFileRepository implements EmbeddedFileRepository {

    public final Map<Long, EmbeddedFile> embeddedFiles = new HashMap<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name){
        calledMethods.add(name);

    }

    private Optional<EmbeddedFile> find(Long id) {
        return Optional.ofNullable(embeddedFiles.get(id));
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        // TODO Auto-generated method stub
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends EmbeddedFile> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<EmbeddedFile> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub
    }

    @Override
    public EmbeddedFile getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EmbeddedFile getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public EmbeddedFile getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    @Override
    public <S extends EmbeddedFile> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return List.of();
    }

    @Override
    public List<EmbeddedFile> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(embeddedFiles.values());
    }

    @Override
    public List<EmbeddedFile> findAllById(Iterable<Long> longs) {
        // TODO Auto-generated method stub
        return List.of();
    }

    @Override
    public <S extends EmbeddedFile> S save(S entity) {
        call("save");
        embeddedFiles.put((long) embeddedFiles.size(), entity);
        return entity;
    }

    @Override
    public Optional<EmbeddedFile> findById(Long id) {
        call("findById");
        return find(id);
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return embeddedFiles.size();
    }

    @Override
    public void deleteById(Long id) {
        embeddedFiles.remove(id);
    }

    @Override
    public void delete(EmbeddedFile entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends EmbeddedFile> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<EmbeddedFile> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return List.of();
    }

    @Override
    public Page<EmbeddedFile> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public <S extends EmbeddedFile> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends EmbeddedFile> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends EmbeddedFile, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
