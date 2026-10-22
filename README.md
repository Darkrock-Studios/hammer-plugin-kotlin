# Hammer plugin template (Kotlin)

A starting point for a [Hammer](https://hammer.ink) plugin written in Kotlin. It builds a working plugin,
**Word count**, which adds a "Count words" item to Project Home's and the scene editor's menus and replies
with each scene's word count. Replace it with your own.

## Make it yours

1. Click **Use this template** on GitHub, or clone this repository.
2. Rename the plugin. In `settings.gradle.kts`, set `rootProject.name`; the built module and package are
   named after it. In `manifest.toml`, set `id` (lowercase, and unique among Hammer's plugins), `name`,
   and `description`.
3. Ask for what your plugin needs. `[permissions] operations` in `manifest.toml` lists the operations it
   may call; the writer sees that list when they install it.
4. Replace `src/commonMain/kotlin/WordCount.kt` with your plugin's logic, and wire it up in
   `src/wasmWasiMain/kotlin/Exports.kt`.
5. Choose a license. This template is MIT; replace `LICENSE` with your own if you prefer.

## Build and test

You need a JDK 17 or newer; the Gradle wrapper does the rest.

```sh
./gradlew jvmTest       # run the tests, on the JVM against a fake Hammer
./gradlew hammerPlugin  # build build/word-count.hammerplugin
```

Install the package in Hammer on desktop: Settings, Plugins, Install plugin. Pick the `.hammerplugin`,
approve its permissions, and restart Hammer.

## Layout

| Path | What |
| --- | --- |
| `manifest.toml` | What the plugin adds to Hammer, and the operations it may call |
| `src/commonMain/kotlin` | The plugin's logic, as common code the tests can reach |
| `src/wasmWasiMain/kotlin/Exports.kt` | The functions Hammer calls, one per kind of extension |
| `src/jvmTest/kotlin` | Tests, with `FakeHammerHost` answering operations and `ActionDriver` stepping through an action |
| `.github/workflows/build.yml` | Tests and packages every push; a `v*` tag makes a GitHub release with the `.hammerplugin` |

The `com.darkrockstudios.hammer.plugin` Gradle plugin sets up Kotlin Multiplatform, adds the kit
(`com.darkrockstudios.hammer:hammer-plugin-kit`), and adds the `hammerPlugin` task. To move to a newer
kit, change its version in `build.gradle.kts`.

## Learn more

- [The Hammer developer site](https://plugins.hammer.ink): the API reference, with the manifest, every
  kind of extension, and all of Hammer's operations.
- [Recipes](https://github.com/Darkrock-Studios/hammer-plugin-sdk/blob/main/RECIPES.md): a small example of
  each kind of plugin.
- [Hammer's own plugins](https://github.com/Darkrock-Studios/hammer-plugins), fuller examples.
