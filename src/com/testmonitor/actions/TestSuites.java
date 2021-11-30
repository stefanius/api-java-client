package com.testmonitor.actions;

import com.testmonitor.api.Connector;
import com.testmonitor.parsers.TestSuiteParser;
import com.testmonitor.parsers.UserParser;
import com.testmonitor.resources.Project;
import com.testmonitor.resources.TestSuite;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestSuites
{
    private final Connector connector;

    private final Integer projectId;

    /**
     * @param connector The TestMonitor connector
     * @param project The TestMonitor project
     */
    public TestSuites(Connector connector, Project project)
    {
        this.connector = connector;
        this.projectId = project.getId();
    }

    /**
     * @return A list of test suites
     */
    public ArrayList<TestSuite> list()
    {
        return this.list(1);
    }

    /**
     * @return A list of test suites
     */
    public ArrayList<TestSuite> list(Integer page)
    {
        return this.list(page, 15);
    }

    /**
     * @return A list of test suites
     */
    public ArrayList<TestSuite> list(Integer page, Integer limit)
    {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("page", page.toString()));
        params.add(new BasicNameValuePair("limit", limit.toString()));
        params.add(new BasicNameValuePair("project_id", this.projectId.toString()));

        return TestSuiteParser.parse(this.connector.get("test-suites", params));
    }

    /**
     * @param id The test suite ID
     *
     * @return The test suite matching the ID
     */
    public TestSuite get(Integer id)
    {
        JSONObject response = this.connector.get("test-suites/" + id);

        HashMap<String, Object> testSuite = (HashMap<String, Object>) response.getJSONObject("data").toMap();

        return TestSuiteParser.parse(testSuite);
    }

    /**
     * Search through test suites.
     *
     * @param query The search keywords
     *
     * @return A list of results
     */
    public ArrayList<TestSuite> search(String keywords)
    {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("project_id", this.projectId.toString()));

        params.add(new BasicNameValuePair("query", keywords));

        return TestSuiteParser.parse(this.connector.get("test-suites", params));
    }

    /**
     * Create a test suite using the provided name.
     *
     * @param name The name of the test suite
     *
     * @return The created test suite
     */
    public TestSuite create(String name)
    {
        TestSuite testSuite = new TestSuite();

        testSuite.setName(name);
        testSuite.setProjectId(this.projectId);

        return this.create(testSuite);
    }

    /**
     * Create a test suite using the test suite object.
     *
     * @param testSuite The test suite object
     *
     * @return The created test suite
     */
    public TestSuite create(TestSuite testSuite)
    {
        JSONObject response = this.connector.post("test-suites", testSuite.toHttpParams());

        HashMap<String, Object> updatedTestSuite = (HashMap<String, Object>) response.getJSONObject("data").toMap();

        return TestSuiteParser.parse(updatedTestSuite);
    }

    /**
     * Find a test suite using the provided keywords or create a new one.
     *
     * @param keywords The search keywords
     *
     * @return A test suite matching the keywords or a new test suite.
     */
    public TestSuite findOrCreate(String keywords)
    {
        ArrayList<TestSuite> testSuites = this.search(keywords);

        if (testSuites.size() > 0) {
            return testSuites.get(0);
        }

        return this.create(keywords);
    }

    /**
     * Update a test suite.
     *
     * @param testSuite The test suite you want to update
     *
     * @return The updated test suite
     */
    public TestSuite update(TestSuite testSuite)
    {
        JSONObject response = this.connector.put("test-suites/" + testSuite.getId(), testSuite.toHttpParams());

        HashMap<String, Object> updatedTestSuite = (HashMap<String, Object>) response.getJSONObject("data").toMap();

        return TestSuiteParser.parse(updatedTestSuite);
    }
}
