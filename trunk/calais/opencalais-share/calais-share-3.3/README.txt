It is standard in Ant based Java projects to provide a build.properties.sample
file. This file should serve as an example of how to create a build.properties
file so that the project will build correctly. Normally this is a matter of
setting directory paths according to your personal development environment.

To deploy without AMP:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy".

To deploy as an AMP:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy-amp.
