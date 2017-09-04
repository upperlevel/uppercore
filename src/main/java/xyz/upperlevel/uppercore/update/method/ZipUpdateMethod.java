package xyz.upperlevel.uppercore.update.method;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public abstract class ZipUpdateMethod implements UpdateMethod {
    @Override
    public void update(File updateFile, Plugin plugin) throws IOException {
        init();
        for (Path root : FileSystems.newFileSystem(updateFile.toURI(), Collections.emptyMap()).getRootDirectories()) {
            Files.walkFileTree(
                    root,
                    new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                            return acceptDir(path) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                            Path out = locate(plugin, path);
                            if(out != null)
                                Files.copy(path, out, REPLACE_EXISTING);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                            finishDir(path);
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
        }
    }

    public void init() {
    }

    public abstract Path locate(Plugin plugin, Path entry);

    public boolean acceptDir(Path dir) {
        return true;
    }

    public void finishDir(Path dir) {
    }
}
