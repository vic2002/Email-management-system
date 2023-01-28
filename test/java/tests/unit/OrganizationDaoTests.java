package tests.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.OrganizationDao;
import server.model.Organization;

class OrganizationDaoTests {
	/*
	private static OrganizationDao OrgDao;
	private Organization org1;
	private Organization org2;
	private static int count;
	
	@BeforeAll
	static void setup() {
		OrgDao = new OrganizationDao();
		count = OrgDao.getAll().size();
	}
	
	@BeforeEach
	void addOrganizations() {
		OrgDao.create("MyOrganization", "myorg@org.com", "myorg.com");
		org1 = OrgDao.getByEmail("myorg@org.com");
		OrgDao.create("AnotherOrg", "anotherorg@yahoo.com", "anotherorganization.com");
		org2 = OrgDao.getByEmail("anotherorg@yahoo.com");
	}
	
	@AfterEach
	void cleanup() {
		OrgDao.deleteById(org1.getId());
		OrgDao.deleteById(org2.getId());
	}
	
	@Test
	void getAllTest() {
		assertEquals(count + 2, OrgDao.getAll().size());
	}
	
	@Test
	void getByIdTest() {
		assertEquals("MyOrganization", OrgDao.getById(org1.getId()).getName());
		assertEquals("AnotherOrg", OrgDao.getById(org2.getId()).getName());
	}
	*/
}
