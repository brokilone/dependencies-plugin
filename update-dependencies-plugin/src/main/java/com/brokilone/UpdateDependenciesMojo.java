package com.brokilone;

import com.brokilone.utils.PomVersionUpdater;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "update-dependencies")
public class UpdateDependenciesMojo extends AbstractMojo {

    @Parameter(defaultValue = "https://search.maven.org/solrsearch/", property = "repo.url")
    private String repositoryUrl;

    @Parameter(defaultValue = "select?q=g:%s+AND+a:%s&core=%s&rows=%s&wt=%s")
    private String selectPattern;

    @Parameter(defaultValue = "1")
    private Integer rowNumber;

    @Parameter(defaultValue = "gav")
    private String sortPattern;

    @Parameter(defaultValue = "json")
    private String format;

    @Parameter
    private String pomPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        new PomVersionUpdater().executeUpdate(
                pomPath,
                getLog(),
                repositoryUrl,
                selectPattern,
                rowNumber,
                sortPattern,
                format
        );
    }
}
