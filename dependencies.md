<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                   | License                          |
| ---------------------------- | -------------------------------- |
| [error-reporting-java][0]    | [MIT][1]                         |
| [BucketFS Java][2]           | [MIT][1]                         |
| [Apache Commons CLI][4]      | [Apache License, Version 2.0][5] |
| [Project Lombok][6]          | [The MIT License][7]             |
| [GitHub API for Java][8]     | [The MIT license][9]             |
| [pgdjbc Postgresql-jre7][10] | [BSD-2-Clause][11]               |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [Hamcrest][12]                                  | [BSD License 3][13]               |
| [JUnit Jupiter (Aggregator)][14]                | [Eclipse Public License v2.0][15] |
| [Test containers for Exasol on Docker][16]      | [MIT][1]                          |
| [Testcontainers :: JUnit Jupiter Extension][18] | [MIT][19]                         |
| [Testcontainers :: JDBC :: PostgreSQL][18]      | [MIT][19]                         |
| [Matcher for SQL Result Sets][22]               | [MIT][1]                          |
| [JUnit][24]                                     | [Eclipse Public License 1.0][25]  |

## Runtime Dependencies

| Dependency            | License                          |
| --------------------- | -------------------------------- |
| [JaCoCo :: Agent][26] | [Eclipse Public License 2.0][27] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [JaCoCo :: Maven Plugin][28]                            | [Eclipse Public License 2.0][27]               |
| [Apache Maven Compiler Plugin][30]                      | [Apache License, Version 2.0][5]               |
| [Apache Maven Assembly Plugin][32]                      | [Apache License, Version 2.0][5]               |
| [Maven Failsafe Plugin][34]                             | [Apache License, Version 2.0][5]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][36] | [ASL2][37]                                     |
| [Versions Maven Plugin][38]                             | [Apache License, Version 2.0][5]               |
| [Apache Maven Enforcer Plugin][40]                      | [Apache License, Version 2.0][5]               |
| [Artifact reference checker and unifier][42]            | [MIT][1]                                       |
| [Project keeper maven plugin][44]                       | [MIT][1]                                       |
| [Maven Dependency Plugin][46]                           | [The Apache Software License, Version 2.0][37] |
| [Reproducible Build Maven Plugin][48]                   | [Apache 2.0][37]                               |
| [Maven Surefire Plugin][50]                             | [Apache License, Version 2.0][5]               |
| [error-code-crawler-maven-plugin][52]                   | [MIT][1]                                       |
| [Maven Clean Plugin][54]                                | [The Apache Software License, Version 2.0][37] |
| [Maven Resources Plugin][56]                            | [The Apache Software License, Version 2.0][37] |
| [Maven JAR Plugin][58]                                  | [The Apache Software License, Version 2.0][37] |
| [Maven Install Plugin][60]                              | [The Apache Software License, Version 2.0][37] |
| [Maven Deploy Plugin][62]                               | [The Apache Software License, Version 2.0][37] |
| [Maven Site Plugin 3][64]                               | [The Apache Software License, Version 2.0][37] |

[26]: https://www.eclemma.org/jacoco/index.html
[44]: https://github.com/exasol/project-keeper-maven-plugin
[2]: https://github.com/exasol/bucketfs-java
[0]: https://github.com/exasol/error-reporting-java
[9]: https://www.opensource.org/licenses/mit-license.php
[10]: https://jdbc.postgresql.org
[37]: http://www.apache.org/licenses/LICENSE-2.0.txt
[6]: https://projectlombok.org
[50]: https://maven.apache.org/surefire/maven-surefire-plugin/
[11]: https://jdbc.postgresql.org/about/license.html
[54]: http://maven.apache.org/plugins/maven-clean-plugin/
[1]: https://opensource.org/licenses/MIT
[34]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[38]: http://www.mojohaus.org/versions-maven-plugin/
[46]: http://maven.apache.org/plugins/maven-dependency-plugin/
[13]: http://opensource.org/licenses/BSD-3-Clause
[30]: https://maven.apache.org/plugins/maven-compiler-plugin/
[19]: http://opensource.org/licenses/MIT
[24]: http://junit.org
[27]: https://www.eclipse.org/legal/epl-2.0/
[25]: http://www.eclipse.org/legal/epl-v10.html
[16]: https://github.com/exasol/exasol-testcontainers
[28]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[7]: https://projectlombok.org/LICENSE
[22]: https://github.com/exasol/hamcrest-resultset-matcher
[48]: http://zlika.github.io/reproducible-build-maven-plugin
[58]: http://maven.apache.org/plugins/maven-jar-plugin/
[5]: https://www.apache.org/licenses/LICENSE-2.0.txt
[40]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[15]: https://www.eclipse.org/legal/epl-v20.html
[60]: http://maven.apache.org/plugins/maven-install-plugin/
[4]: http://commons.apache.org/proper/commons-cli/
[14]: https://junit.org/junit5/
[36]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[18]: https://testcontainers.org
[12]: http://hamcrest.org/JavaHamcrest/
[62]: http://maven.apache.org/plugins/maven-deploy-plugin/
[64]: http://maven.apache.org/plugins/maven-site-plugin/
[8]: https://github-api.kohsuke.org/
[56]: http://maven.apache.org/plugins/maven-resources-plugin/
[42]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[52]: https://github.com/exasol/error-code-crawler-maven-plugin
[32]: https://maven.apache.org/plugins/maven-assembly-plugin/
