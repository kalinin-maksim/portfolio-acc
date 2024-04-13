package edu.kalinin.acc.repository;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.GlCode;

import javax.persistence.EntityNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = GlCode.class)
@EnableJpaRepositories(basePackageClasses = GlCodeRepository.class)
@ContextConfiguration(classes = {
        GlCodeRepository.class
})
@Slf4j
class GlCodeRepositoryTest {

    @Autowired
    private GlCodeRepository glCodeRepository;

    @Test
    void saveTest() {
        var parent = new GlCode();
        parent.setName("parent");
        var child1 = new GlCode();
        child1.setName("child1");
        var child2 = new GlCode();
        child2.setName("child2");
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        glCodeRepository.save(parent);
        glCodeRepository.flush();

        var parentInDB = glCodeRepository.findById(parent.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(parentInDB.getChildren().size(), Matchers.equalTo(2));
    }
}