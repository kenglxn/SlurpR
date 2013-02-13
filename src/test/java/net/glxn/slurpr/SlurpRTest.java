package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;
import net.glxn.slurpr.model.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class SlurpRTest {

    private static final String TEST_CSV = "test.csv";

    @Test
    public void throwsExceptionForFileNotFound() throws Exception {
        String bogusFileName = "foo";
        try {
            SlurpR.csv(bogusFileName);
            fail("expected exception");
        } catch (SlurpRException e) {
            assertTrue(e.getMessage().contains(bogusFileName));
        }
    }

    @Test
    public void slurperInstanceForFoundFile() throws Exception {
        assertNotNull(SlurpR.csv("test.csv"));
    }

    @Test
    public void getsSlurperMapperForType() throws Exception {
        SlurpMapper<Person> slurpMapper = SlurpR.csv(TEST_CSV).to(Person.class);
        assertNotNull(slurpMapper);
    }

    @Ignore
    @Test
    public void getsListOfObjectsFromStream() throws Exception {
        List<Person> persons = SlurpR.csv(TEST_CSV).to(Person.class).list();
        assertNotNull(persons);
        assertEquals(2, persons.size());
    }
}
