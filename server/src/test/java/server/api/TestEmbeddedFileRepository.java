package server.api;

import commons.EmbeddedFile;
import server.database.EmbeddedFileRepository;

import java.util.*;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestEmbeddedFileRepository implements EmbeddedFileRepository {

    public final Map<Long, EmbeddedFile> embeddedFiles = new HashMap<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<EmbeddedFile> findAll() {
        call("findAll");
        return new ArrayList<>(embeddedFiles.values());
    }

    @Override
    public List<EmbeddedFile> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EmbeddedFile> findAllById(Iterable<Long> ids) {
        call("findAllById");
        List<EmbeddedFile> result = new ArrayList<>();
        for (Long id : ids) {
            if (embeddedFiles.containsKey(id)) {
                result.add(embeddedFiles.get(id));
            }
        }
        return result;
    }

    @Override
    public <S extends EmbeddedFile> List<S> saveAll(Iterable<S> entities) {
        call("saveAll");
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            embeddedFiles.put(entity.getId(), entity);
            result.add(entity);
        }
        return result;
    }

    @Override
    public void flush() {
        call("flush");
    }

    @Override
    public <S extends EmbeddedFile> S saveAndFlush(S entity) {
        call("saveAndFlush");
        return save(entity);
    }

    @Override
    public <S extends EmbeddedFile> List<S> saveAllAndFlush(Iterable<S> entities) {
        call("saveAllAndFlush");
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<EmbeddedFile> entities) {
        call("deleteAllInBatch");
        for (EmbeddedFile entity : entities) {
            embeddedFiles.remove(entity.getId());
        }
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        call("deleteAllByIdInBatch");
        for (Long id : ids) {
            embeddedFiles.remove(id);
        }
    }

    @Override
    public void deleteAllInBatch() {
        call("deleteAllInBatch");
        embeddedFiles.clear();
    }

    @Override
    public EmbeddedFile getOne(Long id) {
        call("getOne");
        return embeddedFiles.get(id);
    }

    @Override
    public EmbeddedFile getById(Long id) {
        call("getById");
        return embeddedFiles.get(id);
    }

    @Override
    public EmbeddedFile getReferenceById(Long id) {
        call("getReferenceById");
        return embeddedFiles.get(id);
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
    public Page<EmbeddedFile> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends EmbeddedFile> S save(S entity) {
        call("save");
        embeddedFiles.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<EmbeddedFile> findById(Long id) {
        call("findById");
        return Optional.ofNullable(embeddedFiles.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return embeddedFiles.containsKey(id);
    }

    @Override
    public long count() {
        call("count");
        return embeddedFiles.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        embeddedFiles.remove(id);
    }

    @Override
    public void delete(EmbeddedFile entity) {
        call("delete");
        embeddedFiles.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        call("deleteAllById");
        for (Long id : ids) {
            embeddedFiles.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends EmbeddedFile> entities) {
        call("deleteAllIterable");
        for (EmbeddedFile entity : entities) {
            embeddedFiles.remove(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        call("deleteAll");
        embeddedFiles.clear();
    }

    @Override
    public <S extends EmbeddedFile> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
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
    public <S extends EmbeddedFile, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}


