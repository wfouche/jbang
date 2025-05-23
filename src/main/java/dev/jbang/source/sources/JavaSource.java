package dev.jbang.source.sources;

import static dev.jbang.util.JavaUtil.resolveInJavaHome;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import dev.jbang.devkitman.Jdk;
import dev.jbang.source.*;
import dev.jbang.source.AppBuilder;
import dev.jbang.source.buildsteps.CompileBuildStep;

public class JavaSource extends Source {

	public JavaSource(String script, Function<String, String> replaceProperties) {
		super(script, replaceProperties);
	}

	public JavaSource(ResourceRef script, Function<String, String> replaceProperties) {
		super(script, replaceProperties);
	}

	protected JavaSource(ResourceRef ref, String script, Function<String, String> replaceProperties) {
		super(ref, script, replaceProperties);
	}

	@Override
	public @Nonnull Type getType() {
		return Type.java;
	}

	@Override
	protected List<String> getCompileOptions() {
		return tagReader.collectOptions("JAVAC_OPTIONS", "COMPILE_OPTIONS");
	}

	@Override
	protected List<String> getNativeOptions() {
		return tagReader.collectOptions("NATIVE_OPTIONS");
	}

	@Override
	protected List<String> getRuntimeOptions() {
		return tagReader.collectOptions("JAVA_OPTIONS", "RUNTIME_OPTIONS");
	}

	@Override
	public Builder<CmdGeneratorBuilder> getBuilder(BuildContext ctx) {
		return new JavaAppBuilder(ctx);
	}

	public static class JavaAppBuilder extends AppBuilder {
		public JavaAppBuilder(BuildContext ctx) {
			super(ctx);
		}

		@Override
		protected Builder<Project> getCompileBuildStep() {
			return new JavaCompileBuildStep();
		}

		public class JavaCompileBuildStep extends CompileBuildStep {

			public JavaCompileBuildStep() {
				super(JavaAppBuilder.this.ctx);
			}

			@Override
			protected String getCompilerBinary(String requestedJavaVersion) {
				Project prj = ctx.getProject();
				Jdk jdk = prj.projectJdkManager().getOrInstallJdk(requestedJavaVersion);
				return resolveInJavaHome("javac", jdk);
			}

			@Override
			protected String getMainExtension() {
				return Type.java.extension;
			}
		}
	}
}
