package server.database;

import commons.Collection;
import commons.Note;
import org.hibernate.engine.spi.CollectionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

}
