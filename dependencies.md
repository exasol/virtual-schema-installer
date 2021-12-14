<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                   | License                          |
| ---------------------------- | -------------------------------- |
| [error-reporting-java][0]    | [MIT][1]                         |
| [BucketFS Java][2]           | [MIT][1]                         |
| [Apache Commons CLI][4]      | [Apache License, Version 2.0][5] |
| [GitHub API for Java][6]     | [The MIT license][7]             |
| [EXASolution JDBC Driver][8] | [EXAClient License][9]           |
| [PostgreSQL JDBC Driver][10] | [BSD-2-Clause][11]               |

## Test Dependencies

| Dependency                                      | License                                                |
| ----------------------------------------------- | ------------------------------------------------------ |
| [Hamcrest][12]                                  | [BSD License 3][13]                                    |
| [JUnit Jupiter (Aggregator)][14]                | [Eclipse Public License v2.0][15]                      |
| [Testcontainers :: JDBC :: MySQL][16]           | [MIT][17]                                              |
| [MySQL Connector/J][18]                         | The GNU General Public License, v2 with FOSS exception |
| [Test containers for Exasol on Docker][19]      | [MIT][1]                                               |
| [Testcontainers :: JUnit Jupiter Extension][16] | [MIT][17]                                              |
| [Testcontainers :: JDBC :: PostgreSQL][16]      | [MIT][17]                                              |
| [Matcher for SQL Result Sets][25]               | [MIT][1]                                               |
| [Microsoft JDBC Driver for SQL Server][27]      | [MIT License][28]                                      |
| [Testcontainers :: MS SQL Server][16]           | [MIT][17]                                              |
| [Testcontainers :: JDBC :: DB2][16]             | [MIT][17]                                              |
| IBM Data Server Driver for JDBC and SQLJ        | [International Program License Agreement (IPLA)][33]   |
| [TestContainers :: elasticsearch][16]           | [MIT][17]                                              |
| [rest-high-level][36]                           | [Elastic License 2.0][37]                              |
| [jdbc][36]                                      | [Elastic License 2.0][37]                              |
| [Apache HttpClient][40]                         | [Apache License, Version 2.0][41]                      |
| [Apache Log4j API][42]                          | [Apache License, Version 2.0][5]                       |
| [Testcontainers :: JDBC :: Oracle XE][16]       | [MIT][17]                                              |
| [ojdbc8][46]                                    | Oracle Free Use Terms and Conditions (FUTC)            |

## Runtime Dependencies

| Dependency            | License                          |
| --------------------- | -------------------------------- |
| [JaCoCo :: Agent][47] | [Eclipse Public License 2.0][48] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [JaCoCo :: Maven Plugin][49]                            | [Eclipse Public License 2.0][48]               |
| [Apache Maven Compiler Plugin][51]                      | [Apache License, Version 2.0][5]               |
| [Apache Maven Assembly Plugin][53]                      | [Apache License, Version 2.0][5]               |
| [Maven Failsafe Plugin][55]                             | [Apache License, Version 2.0][5]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][57] | [ASL2][41]                                     |
| [Versions Maven Plugin][59]                             | [Apache License, Version 2.0][5]               |
| [Apache Maven Enforcer Plugin][61]                      | [Apache License, Version 2.0][5]               |
| [Artifact reference checker and unifier][63]            | [MIT][1]                                       |
| [Project keeper maven plugin][65]                       | [MIT][1]                                       |
| [Maven Dependency Plugin][67]                           | [The Apache Software License, Version 2.0][41] |
| [Maven Surefire Plugin][69]                             | [Apache License, Version 2.0][5]               |
| [error-code-crawler-maven-plugin][71]                   | [MIT][1]                                       |
| [Apache Maven JAR Plugin][73]                           | [Apache License, Version 2.0][5]               |
| [Reproducible Build Maven Plugin][75]                   | [Apache 2.0][41]                               |
| [Maven Clean Plugin][77]                                | [The Apache Software License, Version 2.0][41] |
| [Maven Resources Plugin][79]                            | [The Apache Software License, Version 2.0][41] |
| [Maven Install Plugin][81]                              | [The Apache Software License, Version 2.0][41] |
| [Maven Deploy Plugin][83]                               | [The Apache Software License, Version 2.0][41] |
| [Maven Site Plugin 3][85]                               | [The Apache Software License, Version 2.0][41] |

[47]: https://www.eclemma.org/jacoco/index.html
[65]: https://github.com/exasol/project-keeper-maven-plugin
[2]: https://github.com/exasol/bucketfs-java
[0]: https://github.com/exasol/error-reporting-java
[37]: https://raw.githubusercontent.com/elastic/elasticsearch/v7.16.1/licenses/ELASTIC-LICENSE-2.0.txt
[7]: https://www.opensource.org/licenses/mit-license.php
[46]: https://www.oracle.com/database/technologies/appdev/jdbc.html
[41]: http://www.apache.org/licenses/LICENSE-2.0.txt
[69]: https://maven.apache.org/surefire/maven-surefire-plugin/
[11]: https://jdbc.postgresql.org/about/license.html
[77]: http://maven.apache.org/plugins/maven-clean-plugin/
[9]: https://docs.exasol.com/connect_exasol/drivers/jdbc.htm
[1]: https://opensource.org/licenses/MIT
[59]: http://www.mojohaus.org/versions-maven-plugin/
[13]: http://opensource.org/licenses/BSD-3-Clause
[51]: https://maven.apache.org/plugins/maven-compiler-plugin/
[36]: https://github.com/elastic/elasticsearch
[48]: https://www.eclipse.org/legal/epl-2.0/
[49]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[25]: https://github.com/exasol/hamcrest-resultset-matcher
[75]: http://zlika.github.io/reproducible-build-maven-plugin
[28]: http://www.opensource.org/licenses/mit-license.php
[18]: http://dev.mysql.com/doc/connector-j/en/
[27]: https://github.com/Microsoft/mssql-jdbc
[14]: https://junit.org/junit5/
[12]: http://hamcrest.org/JavaHamcrest/
[6]: https://github-api.kohsuke.org/
[79]: http://maven.apache.org/plugins/maven-resources-plugin/
[63]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[73]: https://maven.apache.org/plugins/maven-jar-plugin/
[10]: https://jdbc.postgresql.org
[4]: https://commons.apache.org/proper/commons-cli/
[42]: https://logging.apache.org/log4j/2.x/log4j-api/
[55]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[67]: http://maven.apache.org/plugins/maven-dependency-plugin/
[17]: http://opensource.org/licenses/MIT
[19]: https://github.com/exasol/exasol-testcontainers
[5]: https://www.apache.org/licenses/LICENSE-2.0.txt
[61]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[8]: http://www.exasol.com
[15]: https://www.eclipse.org/legal/epl-v20.html
[81]: http://maven.apache.org/plugins/maven-install-plugin/
[40]: http://hc.apache.org/httpcomponents-client
[57]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[16]: https://testcontainers.org
[33]: http://www-03.ibm.com/software/sla/sladb.nsf/lilookup/B290E426DA2F1ECC852586FC006262BE?OpenDocument
[83]: http://maven.apache.org/plugins/maven-deploy-plugin/
[85]: http://maven.apache.org/plugins/maven-site-plugin/
[71]: https://github.com/exasol/error-code-crawler-maven-plugin
[53]: https://maven.apache.org/plugins/maven-assembly-plugin/
