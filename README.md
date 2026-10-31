
# Fascicolo Sanitario 2.0

# _it-fse-srv-syntax-rules-manager_

Il microservizio in oggetto espone API che consentono la creazione, lettura, aggiornamento ed eliminazione degli artefatti per i controlli sintattici utilizzati dal Gateway in fase di validazione. Il microservizio, inoltre, espone API necessarie al Gateway per effettuare l'allineamento dei nuovi artefatti.

<br/>

## Repos
- [*it-fse-support*](https://github.com/ministero-salute/it-fse-support) - Pagina di supporto FSE 2.0
- [*it-fse-landing*](https://github.com/ministero-salute/it-fse-landing) - Landing page dei repository FSE 2.0

<br/>

## License

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
# Isolation tests

Run the self-contained suite from this directory with Maven and JDK 17:

```bash
mvn -PISOLATION clean test
```

If your default JDK is newer, select JDK 17 first (`JAVA_HOME` and `PATH`).
The existing Mockito/Byte Buddy versions do not support Java 21 instrumentation.

The opt-in Maven profile selects JUnit tests tagged `ISOLATION`; ordinary Maven
test runs exclude that tag and retain the existing tests. The minimal Spring
test graph activates the `ISOLATION` Spring profile and wires real controllers,
services, and repositories with a mocked `MongoTemplate`. It does not start the
Boot application, a web server, MongoDB, containers, or remote services.

Tests assert CRUD responses and content, changeset mapping and time-bound queries,
soft-deletion and bulk-write integrity checks, database error translation, schema
compilation and includes, multipart validation, problem details and trace IDs,
OpenAPI customization, collection naming, and sample-file loading. The runner
tests read the existing schema fixtures without modifying them.

JaCoCo produces `target/site/jacoco/index.html` and
`target/site/jacoco/jacoco.xml`.
