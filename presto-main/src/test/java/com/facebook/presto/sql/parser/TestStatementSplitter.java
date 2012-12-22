package com.facebook.presto.sql.parser;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.facebook.presto.sql.parser.StatementSplitter.squeezeStatement;
import static org.testng.Assert.assertEquals;

public class TestStatementSplitter
{
    @Test
    public void testSplitterIncomplete()
    {
        StatementSplitter splitter = new StatementSplitter(" select * FROM foo  ");
        assertEquals(splitter.getCompleteStatements(), ImmutableList.of());
        assertEquals(splitter.getPartialStatement(), "select * FROM foo");
    }

    @Test
    public void testSplitterSingle()
    {
        StatementSplitter splitter = new StatementSplitter("select * from foo;");
        assertEquals(splitter.getCompleteStatements(), ImmutableList.of("select * from foo"));
        assertEquals(splitter.getPartialStatement(), "");
    }

    @Test
    public void testSplitterMultiple()
    {
        StatementSplitter splitter = new StatementSplitter(" select * from  foo ; select * from t; select * from ");
        assertEquals(splitter.getCompleteStatements(), ImmutableList.of("select * from  foo", "select * from t"));
        assertEquals(splitter.getPartialStatement(), "select * from");
    }

    @Test
    public void testSplitterErrorBeforeComplete()
    {
        StatementSplitter splitter = new StatementSplitter(" select * from foo @ ; select ");
        assertEquals(splitter.getCompleteStatements(), ImmutableList.of());
        assertEquals(splitter.getPartialStatement(), "select * from foo @ ; select");
    }

    @Test
    public void testSplitterErrorAfterComplete()
    {
        StatementSplitter splitter = new StatementSplitter("select * from foo; select z@ oops ");
        assertEquals(splitter.getCompleteStatements(), ImmutableList.of("select * from foo"));
        assertEquals(splitter.getPartialStatement(), "select z@ oops");
    }

    @Test
    public void testSqueezeStatement()
    {
        String sql = "select   *  from\n foo\n  order by x ; ";
        assertEquals(squeezeStatement(sql), "select * from foo order by x ;");
    }
}
