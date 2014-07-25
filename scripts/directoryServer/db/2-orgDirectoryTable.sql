CREATE TABLE `org_directory` (
  `organization_id` int(11) NOT NULL COMMENT 'Unique Identifier to be concatenated with the Organization Id\nOrganization to form a global unique ID.',
  `org_name` varchar(64) NOT NULL COMMENT 'OPEID - Office of Postsecondary Education identification code. Number issued to colleges that are eligible to\nparticipate in federal financial aid programs\nIPEDS - Integrated Postsecondary Education Data System ID\nATP - College Board Admissions Testing Program, codes maintained by ETS\nFICE - Federal Interagency Committee on Education\nACT - American College Testing program\nCCD\nCEEB - College Entrance Examination Board ID. Unique ID for high school, college, or university\nPSIS - Statistics Canada Organization ID\nUSIS\nESIS\nDUNS - A unique nine digit identification number, for each physical location of a business.\nNCHELP ID',
  `org_subcode` varchar(45) DEFAULT NULL COMMENT 'Further refinement of the delivery location. Ex, department\n(Math, Science)',
  `description` text,
  `credential` varchar(128) NOT NULL COMMENT 'Unique for each sender and receiver',
  `contact_type` varchar(45) NOT NULL COMMENT 'Admin, Technical, Billing, etc…',
  `address` varchar(128) NOT NULL COMMENT 'Mailing/Billing Address of organization',
  `email` varchar(100) NOT NULL COMMENT 'Email address of organization',
  `phone` varchar(20) NOT NULL COMMENT 'Phone Number of organization',
  `url` varchar(255) NOT NULL COMMENT 'Web Site address of organization',
  `ein` varchar(11) NOT NULL COMMENT 'Employer Identification Number (EIN) is also known as a Federal Tax Identification Number, and is used to identify a business entity',
  `entity_indicator` tinyint(1) unsigned NOT NULL COMMENT 'Identifies if organization is a Vendor or an Institution',
  `terms_of_use` text NOT NULL COMMENT 'Rules which one must agree to abide by in order to use a service. Terms of service can also be merely a disclaimer, especially regarding the use of websites.',
  `privacy_policy` text NOT NULL COMMENT 'A statement that discloses some or all of the ways a party\ngathers, uses, discloses and manages a customer or client''s \ndata.',
  `receiving_format` varchar(24) NOT NULL COMMENT 'Data format the organization is able to process',
  PRIMARY KEY (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
