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
| [pgdjbc Postgresql-jre7][10] | [BSD-2-Clause][11]               |

## Test Dependencies

| Dependency                                      | License                                                   |
| ----------------------------------------------- | --------------------------------------------------------- |
| [Hamcrest][12]                                  | [BSD License 3][13]                                       |
| [JUnit Jupiter (Aggregator)][14]                | [Eclipse Public License v2.0][15]                         |
| [Testcontainers :: JDBC :: MySQL][16]           | [MIT][17]                                                 |
| [MySQL Connector/J][18]                         | The GNU General Public License, v2 with FOSS exception    |
| [Test containers for Exasol on Docker][19]      | [MIT][1]                                                  |
| [Testcontainers :: JUnit Jupiter Extension][16] | [MIT][17]                                                 |
| [Testcontainers :: JDBC :: PostgreSQL][16]      | [MIT][17]                                                 |
| [Matcher for SQL Result Sets][25]               | [MIT][1]                                                  |
| [JUnit][27]                                     | [Eclipse Public License 1.0][28]                          |
| [Microsoft JDBC Driver for SQL Server][29]      | [MIT License][30]                                         |
| [Testcontainers :: MS SQL Server][16]           | [MIT][17]                                                 |
| [Testcontainers :: JDBC :: DB2][16]             | [MIT][17]                                                 |
| IBM Data Server Driver for JDBC and SQLJ        | [International Program License Agreement (IPLA)][35]      |
| [TestContainers :: elasticsearch][16]           | [MIT][17]                                                 |
| [rest-high-level][38]                           | [Elastic License 2.0][39]                                 |
| [jdbc][38]                                      | [Elastic License 2.0][39]                                 |
| [JSR 374 (JSON Processing) API][42]             | [Dual license consisting of the CDDL v1.1 and GPL v2][43] |
| [Apache HttpClient][44]                         | [Apache License, Version 2.0][45]                         |

## Runtime Dependencies

| Dependency            | License                          |
| --------------------- | -------------------------------- |
| [JaCoCo :: Agent][46] | [Eclipse Public License 2.0][47] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [JaCoCo :: Maven Plugin][48]                            | [Eclipse Public License 2.0][47]               |
| [Apache Maven Compiler Plugin][50]                      | [Apache License, Version 2.0][5]               |
| [Apache Maven Assembly Plugin][52]                      | [Apache License, Version 2.0][5]               |
| [Maven Failsafe Plugin][54]                             | [Apache License, Version 2.0][5]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][56] | [ASL2][45]                                     |
| [Versions Maven Plugin][58]                             | [Apache License, Version 2.0][5]               |
| [Apache Maven Enforcer Plugin][60]                      | [Apache License, Version 2.0][5]               |
| [Artifact reference checker and unifier][62]            | [MIT][1]                                       |
| [Project keeper maven plugin][64]                       | [MIT][1]                                       |
| [Maven Dependency Plugin][66]                           | [The Apache Software License, Version 2.0][45] |
| [Maven Surefire Plugin][68]                             | [Apache License, Version 2.0][5]               |
| [error-code-crawler-maven-plugin][70]                   | [MIT][1]                                       |
| [Apache Maven JAR Plugin][72]                           | [Apache License, Version 2.0][5]               |
| [Reproducible Build Maven Plugin][74]                   | [Apache 2.0][45]                               |
| [Maven Clean Plugin][76]                                | [The Apache Software License, Version 2.0][45] |
| [Maven Resources Plugin][78]                            | [The Apache Software License, Version 2.0][45] |
| [Maven Install Plugin][80]                              | [The Apache Software License, Version 2.0][45] |
| [Maven Deploy Plugin][82]                               | [The Apache Software License, Version 2.0][45] |
| [Maven Site Plugin 3][84]                               | [The Apache Software License, Version 2.0][45] |

[46]: https://www.eclemma.org/jacoco/index.html
[64]: https://github.com/exasol/project-keeper-maven-plugin
[2]: https://github.com/exasol/bucketfs-java
[0]: https://github.com/exasol/error-reporting-java
[7]: https://www.opensource.org/licenses/mit-license.php
[42]: https://javaee.github.io/jsonp
[45]: http://www.apache.org/licenses/LICENSE-2.0.txt
[68]: https://maven.apache.org/surefire/maven-surefire-plugin/
[11]: https://jdbc.postgresql.org/about/license.html
[76]: http://maven.apache.org/plugins/maven-clean-plugin/
[9]: https://docs.exasol.com/connect_exasol/drivers/jdbc.htm
[1]: https://opensource.org/licenses/MIT
[58]: http://www.mojohaus.org/versions-maven-plugin/
[13]: http://opensource.org/licenses/BSD-3-Clause
[50]: https://maven.apache.org/plugins/maven-compiler-plugin/
[43]: https://oss.oracle.com/licenses/CDDL+GPL-1.1
[38]: https://github.com/elastic/elasticsearch
[27]: http://junit.org
[47]: https://www.eclipse.org/legal/epl-2.0/
[48]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[25]: https://github.com/exasol/hamcrest-resultset-matcher
[74]: http://zlika.github.io/reproducible-build-maven-plugin
[30]: http://www.opensource.org/licenses/mit-license.php
[18]: http://dev.mysql.com/doc/connector-j/en/
[29]: https://github.com/Microsoft/mssql-jdbc
[14]: https://junit.org/junit5/
[12]: http://hamcrest.org/JavaHamcrest/
[6]: https://github-api.kohsuke.org/
[78]: http://maven.apache.org/plugins/maven-resources-plugin/
[62]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[72]: https://maven.apache.org/plugins/maven-jar-plugin/
[10]: https://jdbc.postgresql.org
[54]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[66]: http://maven.apache.org/plugins/maven-dependency-plugin/
[17]: http://opensource.org/licenses/MIT
[28]: http://www.eclipse.org/legal/epl-v10.html
[19]: https://github.com/exasol/exasol-testcontainers
[5]: https://www.apache.org/licenses/LICENSE-2.0.txt
[39]: https://raw.githubusercontent.com/elastic/elasticsearch/v7.13.4/licenses/ELASTIC-LICENSE-2.0.txt
[60]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[8]: http://www.exasol.com
[15]: https://www.eclipse.org/legal/epl-v20.html
[80]: http://maven.apache.org/plugins/maven-install-plugin/
[4]: http://commons.apache.org/proper/commons-cli/
[44]: http://hc.apache.org/httpcomponents-client
[56]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[16]: https://testcontainers.org
[35]: http://www-03.ibm.com/software/sla/sladb.nsf/lilookup/179A6D1769B0A44D8525862400329FB8?OpenDocument
[82]: http://maven.apache.org/plugins/maven-deploy-plugin/
[84]: http://maven.apache.org/plugins/maven-site-plugin/
[70]: https://github.com/exasol/error-code-crawler-maven-plugin
[52]: https://maven.apache.org/plugins/maven-assembly-plugin/
