# StaffPlusPlus CraftBukkit Compatibility

This repository contains version-specific CraftBukkit/Spigot compatibility modules used by `StaffPlusPlusCraftbukkitAPI`.

## Adding Support For A New Spigot Version

Use the latest matching module as the template. For example, `v26_2_R0` was created by copying `v26_1_R0`.

1. Copy the previous module directory.

   ```sh
   cp -R v26_2_R0 v26_3_R0
   ```

2. Rename the copied Java files and classes.

   For a module named `v26_3_R0`, rename:

   - `Protocol_v26_2_R0.java` to `Protocol_v26_3_R0.java`
   - `PacketHandler_v26_2_R0.java` to `PacketHandler_v26_3_R0.java`
   - `JsonSender_v26_2_R0.java` to `JsonSender_v26_3_R0.java`

   Then replace class references inside those files, including the packet handler construction in `Protocol_*`.

3. Update the new module POM.

   In `v26_3_R0/pom.xml`:

   - set `<artifactId>` to the new module name
   - set `<spigot.version>` to the new Spigot dependency, for example `26.3-R0.1-SNAPSHOT`
   - keep the parent version aligned with the root project version

4. Register the module in the root `pom.xml`.

   Add the new module before `StaffPlusPlusCraftbukkitAPI`:

   ```xml
   <module>v26_3_R0</module>
   ```

5. Add the new module as an API dependency.

   In `StaffPlusPlusCraftbukkitAPI/pom.xml`, add:

   ```xml
   <dependency>
       <groupId>net.shortninja.staffplus</groupId>
       <artifactId>v26_3_R0</artifactId>
       <version>${project.parent.version}</version>
   </dependency>
   ```

   New `v26_*` modules currently follow the `v26_1_R0` pattern and do not use the `remapped-spigot` classifier.

6. Register runtime selection in the API factories.

   In `ProtocolFactory`, add a case for the Bukkit/Paper version string returned by `VersionUtil.getMcVersion()`:

   ```java
   case "26.3-R0.1":
       return new Protocol_v26_3_R0();
   ```

   In `JsonSenderFactory`, add:

   ```java
   case "26.3-R0.1":
       return new JsonSender_v26_3_R0();
   ```

7. Bump the release version when the new support line should be published.

   Update the root `<version>` and every child POM parent `<version>`, including:

   - `StaffPlusPlusCraftbukkitAPI/pom.xml`
   - `StaffPlusPlusCraftbukkitCommon/pom.xml`
   - every existing `v*_R*` module POM
   - the new module POM

8. Update the publish workflow.

   In `.github/workflows/maven-publish.yml`, make sure the new release line selects the correct JDK.

   For a Java 25 release line such as `26.3`, add it to the `Set up JDK 25` condition:

   ```yaml
   if: ${{ steps.get-release-version.outputs.releaseVersion == '26.1' || steps.get-release-version.outputs.releaseVersion == '26.2' || steps.get-release-version.outputs.releaseVersion == '26.3' }}
   ```

   Also exclude the same release line from the `Set up JDK 8` fallback condition. Otherwise a `release/26.3.0` publish run can fall through to Java 8.

   The regular `.github/workflows/maven.yml` build currently uses JDK 25 unconditionally.

9. Verify with Maven.

   The project is configured for Java 25, so make sure Maven is running with JDK 25:

   ```sh
   JAVA_HOME=/usr/lib/jvm/java-25-openjdk mvn -pl v26_3_R0,StaffPlusPlusCraftbukkitAPI -am package -DskipTests
   ```

   If Maven resolves metadata for a new Spigot snapshot but the timestamped jar is missing from Nexus, the code wiring can be correct while the build still fails on dependency resolution. Check:

   ```sh
   curl -fsSL https://nexus.staffplusplus.org/repository/staffplusplus/org/spigotmc/spigot/maven-metadata.xml
   curl -fsSL https://nexus.staffplusplus.org/repository/staffplusplus/org/spigotmc/spigot/26.3-R0.1-SNAPSHOT/maven-metadata.xml
   ```

10. Clean generated output before committing.

   Do not commit `target/` directories. A quick check should show only source and POM changes:

   ```sh
   git status --short
   ```
