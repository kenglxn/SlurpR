# SlurpR

a simple CSV slurping api for java

### Examples:

##### Slurp simple csv where fields in csv match fields in POJO:
```java
List<Person> persons = 
                SlurpR.csv("data.csv")
                      .to(Person.class)
                      .list();
// data.csv
id,name,age
1,john,30
2,mary,28
```

##### Slurp csv using a mapping file:
```java
List<Person> persons =
                SlurpR.csv("data.csv")
                      .to(Person.class)
                      .usingMapping("mapping.json")
                      .list();
                        
// data.csv
Identifaciones,Namos,Alderos
1,john,30
2,mary,28

// mapping.json
{
  "id": "Identifaciones",
  "name": "Namos",
  "age": "Alderos"
}           
```

##### Slurp csv using a mapping file with a LookupProvider
```java
List<Person> persons =
                SlurpR.csv("data.csv")
                      .to(Person.class)
                      .usingMapping("mapping.json")
                      .list();

// data.csv
Identifaciones,Namos,Alderos,Relationes
1,john,30,2
2,mary,28,1

// mapping.json
{
    "id": "Identifaciones",
    "name": "Namos",
    "age": "Alderos",
    "relation": {
        "key": "Relationes",
        "provider": "net.glxn.slurpr.provider.PersonProvider"
    }
}

// PersonProvider
public class PersonProvider implements LookupProvider<Person> {
    @Override
    public Person lookup(String key) {
        return findPerson(key);
    }
}
```

##### Spring support: Slurp csv using a mapping file with a LookupProvider as spring bean
```java
List<Person> persons =
                SlurpR.csv("data.csv")
                      .to(Person.class)
                      .usingMapping("mapping.json")
                      .usingContext(applicationContext)
                      .list();

// data.csv
Identifaciones,Namos,Alderos,Relationes
1,john,30,2
2,mary,28,1

// mapping.json
{
    "id": "Identifaciones",
    "name": "Namos",
    "age": "Alderos",
    "relation": {
        "key": "Relationes",
        "provider": "SPRING"
    }
}

// PersonProvider
public class PersonProvider implements LookupProvider<Person> {
    @Override
    public Person lookup(String key) {
        return personDao.findPerson(key);
    }
    
    @Autowired
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```
