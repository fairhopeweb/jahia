package org.jahia.services.versioning;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jahia.api.Constants;
import org.jahia.bin.Jahia;
import org.jahia.params.ProcessingContext;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPublicationService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.test.TestHelper;

import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

/**
 * Created by IntelliJ IDEA.
 * User: ktlili
 * Date: Mar 5, 2010
 * Time: 4:57:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class VersioningTest extends TestCase {
    private static Logger logger = Logger.getLogger(VersioningTest.class);
    private JahiaSite site;
    private ProcessingContext ctx;
    private final static String TESTSITE_NAME = "jcrVersioningTest";
    private final static String SITECONTENT_ROOT_NODE = "/sites/" + TESTSITE_NAME;

    protected void setUp() throws Exception {
        try {
            site = TestHelper.createSite(TESTSITE_NAME);
            ctx = Jahia.getThreadParamBean();
            assertNotNull(site);
        } catch (Exception ex) {
            logger.warn("Exception during test setUp", ex);
        }
    }


    /**
     * Test number of version after publication
     *
     * @throws Exception
     */
    public void testVersions() throws Exception {
        try {
            JCRPublicationService jcrService = ServicesRegistry.getInstance().getJCRPublicationService();


            JCRSessionWrapper session = jcrService.getSessionFactory().getCurrentUserSession();
            JCRSessionWrapper liveSession = jcrService.getSessionFactory().getCurrentUserSession(Constants.LIVE_WORKSPACE);


            JCRNodeWrapper stageRootNode = session.getNode(SITECONTENT_ROOT_NODE);

            // get home page
            JCRNodeWrapper stageNode = stageRootNode.getNode("home");

            session.checkout(stageNode);
            JCRNodeWrapper stagedSubPage = stageNode.addNode("home_subpage1", "jnt:page");
            stagedSubPage.setProperty("jcr:title", "title");
            session.save();

            // publish it
            jcrService.publish(stageNode.getPath(), Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, null, false, false);


            for (int i = 0; i < 10; i++) {
                session.checkout(stagedSubPage);
                stagedSubPage.setProperty("jcr:title", "title" + i);
                session.save();

                // each time the node i published, a new version should be created
                jcrService.publish(stagedSubPage.getPath(), Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, null, false, false);
            }

            // check number of versions
            JCRNodeWrapper subPagePublishedNode = liveSession.getNode(stagedSubPage.getPath());

            VersionIterator versions = subPagePublishedNode.getVersionHistory().getAllLinearVersions();//getAllVersions();
            int index = 0;
            while (versions.hasNext()) {
                Version v = versions.nextVersion();
                JCRNodeWrapper versionNode = subPagePublishedNode.getFrozenVersion(v.getName());
                String versionTitle = versionNode.getPropertyAsString("jcr:title");
                logger.debug("version number:"+v.getName() +", jcr:title: " + versionTitle);
                index++;
            }
            logger.debug("number of version: " + index);
            assertEquals(10, index);

            versions = subPagePublishedNode.getVersionHistory().getAllVersions();
            for (int i = 0; i < 10; i++) {
                String title = "title" + i;
                JCRNodeWrapper versionNode = subPagePublishedNode.getFrozenVersion(versions.nextVersion().getName());
                String versionTitle = versionNode.getPropertyAsString("jcr:title");
                logger.debug("versionTitle: " + versionTitle);
                assertEquals(title, versionTitle);
            }


        } catch (Exception ex) {
            logger.warn("Exception during test", ex);
        }
    }


    @Override
    protected void tearDown() throws Exception {
        try {
            TestHelper.deleteSite(TESTSITE_NAME);
        } catch (Exception ex) {
            logger.warn("Exception during test tearDown", ex);
        }
    }

}
