/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js tools             **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013-2014, LAMP/EPFL   **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */


package org.scalajs.core.tools.linker

import org.scalajs.core.tools.linker.frontend.LinkerFrontend
import org.scalajs.core.tools.linker.frontend.optimizer.IncOptimizer
import org.scalajs.core.tools.linker.backend._

trait LinkerPlatformExtensions { this: Linker.type =>
  @deprecated("Use StandardLinker.apply() instead.", "0.6.18")
  def apply(semantics: Semantics, outputMode: OutputMode,
      moduleKind: ModuleKind, config: Config): Linker = {
    applyInternal(semantics, outputMode, moduleKind, config)
  }

  private[linker] def applyInternal(semantics: Semantics,
      outputMode: OutputMode, moduleKind: ModuleKind,
      config: Config): Linker = {

    val optOptimizerFactory = {
      if (!config.optimizer) None
      else Some(IncOptimizer.factory)
    }

    val frontend = new LinkerFrontend(semantics, outputMode.esLevel,
        config.sourceMap, config.frontendConfig, optOptimizerFactory)

    val backend = new BasicLinkerBackend(semantics, outputMode, moduleKind,
        config.sourceMap, config.backendConfig)

    new Linker(frontend, backend)
  }

  @deprecated("Use StandardLinker.apply() instead.", "0.6.13")
  def apply(
      semantics: Semantics = Semantics.Defaults,
      outputMode: OutputMode = OutputMode.Default,
      withSourceMap: Boolean = true,
      disableOptimizer: Boolean = false,
      frontendConfig: LinkerFrontend.Config = LinkerFrontend.Config(),
      backendConfig: LinkerBackend.Config = LinkerBackend.Config()): Linker = {

    val config = Config()
      .withSourceMap(withSourceMap)
      .withOptimizer(!disableOptimizer)
      .withFrontendConfig(frontendConfig)
      .withBackendConfig(backendConfig)

    apply(semantics, outputMode, ModuleKind.NoModule, config)
  }
}

object LinkerPlatformExtensions {
  import Linker.Config

  final class ConfigExt(val config: Config) extends AnyVal {
    /** Whether to actually use the Google Closure Compiler pass.
     *
     *  On the JavaScript platform, this always returns `false`, as GCC is not
     *  available.
     */
    def closureCompiler: Boolean = false
  }
}
