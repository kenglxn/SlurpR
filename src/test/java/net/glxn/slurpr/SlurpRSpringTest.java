package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;
import net.glxn.slurpr.model.*;
import org.junit.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

import java.util.*;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:testContext.xml")
public class SlurpRSpringTest extends AbstractJUnit4SpringContextTests {

    @Test
    public void shouldSupportProviderAsSpringBean() throws Exception {
        List<Person> persons =
                SlurpR.csv("test-with-mapping.csv")
                      .to(Person.class)
                      .usingMapping("test-mapping-spring-provider.json")
                      .usingContext(applicationContext)
                      .list();
        assertNotNull(persons);
        assertEquals(2, persons.size());
        assertEquals("1", persons.get(0).getId());
        assertEquals("ken", persons.get(0).getName());
        assertEquals("30", persons.get(0).getAge());
        assertEquals("2", persons.get(0).getRelation().getId());
        assertEquals("2", persons.get(1).getId());
        assertEquals("karianne", persons.get(1).getName());
        assertEquals("28", persons.get(1).getAge());
        assertEquals("1", persons.get(1).getRelation().getId());
    }

    @Test
    public void shouldThrowExceptionIfNoSpringContextAssignedAndMappingSaysToUseSpring() throws Exception {
        try {
            SlurpR.csv("test-with-mapping.csv")
                  .to(Person.class)
                  .usingMapping("test-mapping-spring-provider.json")
                  .list();
            fail("expected exception");
        } catch (SlurpRException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("usingContext(context)"));
        }
    }
}
