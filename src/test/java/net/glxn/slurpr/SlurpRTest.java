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

    @Test
    public void getsListOfObjectsFromStream() throws Exception {
        List<Person> persons = SlurpR.csv(TEST_CSV).to(Person.class).list();
        assertNotNull(persons);
        assertEquals(2, persons.size());
        assertEquals("1", persons.get(0).getId());
        assertEquals("ken", persons.get(0).getName());
        assertEquals("30", persons.get(0).getAge());
        assertEquals("2", persons.get(1).getId());
        assertEquals("karianne", persons.get(1).getName());
        assertEquals("28", persons.get(1).getAge());
    }
}
