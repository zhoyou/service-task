package com.github.hollykunge.security.helper;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: zhhongyu
 * @description: 任务管理用jgit格式化规则
 * @since: Create in 15:26 2019/9/20
 */
public class IDiffFormatter extends DiffFormatter {

    public IDiffFormatter(OutputStream out) {
        super(out);
    }

    @Override
    protected void formatGitDiffFirstHeaderLine(ByteArrayOutputStream o, DiffEntry.ChangeType type, String oldPath, String newPath) throws IOException {
    }

    @Override
    protected void formatIndexLine(OutputStream o, DiffEntry ent) throws IOException {
    }

    @Override
    protected void writeAddedLine(RawText text, int line) throws IOException {
        super.writeAddedLine(text, line);
    }

    @Override
    protected void writeContextLine(RawText text, int line) throws IOException {
        super.writeContextLine(text, line);
    }

    @Override
    protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException {
    }

    @Override
    protected void writeLine(char prefix, RawText text, int cur) throws IOException {
        super.writeLine(prefix, text, cur);
    }

    @Override
    protected void writeRemovedLine(RawText text, int line) throws IOException {
        super.writeRemovedLine(text, line);
    }
}
