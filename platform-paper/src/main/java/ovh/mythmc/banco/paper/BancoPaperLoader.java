package ovh.mythmc.banco.paper;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;

public final class BancoPaperLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        resolver.addDependency(new Dependency(new DefaultArtifact("org.postgresql:postgresql:42.7.9"), null));

        classpathBuilder.addLibrary(resolver);
    }
    
}
