DROP INDEX IF EXISTS speaker_name_idx;
DROP TABLE IF EXISTS conference;
DROP TABLE IF EXISTS speaker;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS speaker_by_conference;
DROP TABLE IF EXISTS conference_by_speaker;
DROP TABLE IF EXISTS report_by_conference;
DROP TABLE IF EXISTS report_by_speaker;
DROP TABLE IF EXISTS report_by_tag;
DROP TABLE IF EXISTS report_stats_by_conference;
DROP TABLE IF EXISTS report_stats_by_speaker_for_year;
DROP TABLE IF EXISTS report_stats_by_speaker_for_conference;
DROP TABLE IF EXISTS report_stats_by_company;
DROP TYPE IF EXISTS speaker_company;
DROP TYPE IF EXISTS report_source;
DROP TYPE IF EXISTS conference_short_info;
DROP TYPE IF EXISTS contact_info;
DROP TYPE IF EXISTS speaker_short_info;
DROP TYPE IF EXISTS speaker_full_info;

CREATE TYPE IF NOT EXISTS speaker_company (
    added_date timestamp,
    name text,
    year int
);

CREATE TYPE IF NOT EXISTS report_source (
    presentation_url text,
    repo_url text,
    video_url text,
);

CREATE TYPE IF NOT EXISTS conference_short_info (
    logo text,
    name text,
    year int,
);

CREATE TYPE IF NOT EXISTS contact_info (
    companies set<frozen<speaker_company>>,
    email text,
    location text,
    twitter_username text
);

CREATE TYPE IF NOT EXISTS speaker_short_info (
    id uuid,
    contact_info frozen<contact_info>,
    name text
);

CREATE TYPE IF NOT EXISTS speaker_full_info (
    id uuid,
    avatar text,
    bio text,
    contact_info frozen<contact_info>,
    name text
);

CREATE TABLE IF NOT EXISTS conference (
    name text,
    year int,
    location text,
    logo text STATIC,
    url text,
    PRIMARY KEY (name, year)
) WITH CLUSTERING ORDER BY (year DESC);

CREATE INDEX IF NOT EXISTS speaker_name_idx ON speaker(name);

CREATE TABLE IF NOT EXISTS speaker (
    id uuid,
    avatar text,
    bio text,
    contact_info frozen<contact_info>,
    name text,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS report_title_idx ON report(title);

CREATE TABLE IF NOT EXISTS report (
    id uuid,
    complexity int,
    conferences set<frozen<conference_short_info>>,
    description text,
    language text,
    source frozen<report_source>,
    speakers set<frozen<speaker_full_info>>,
    tags set<text>,
    title text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS company (
    name text,
    logo text,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS speaker_by_conference (
    conference_name text,
    year int,
    name text,
    id uuid,
    avatar text,
    location text,
    PRIMARY KEY (conference_name, year, name, id)
) WITH CLUSTERING ORDER BY (year DESC, name ASC, id ASC);

CREATE TABLE IF NOT EXISTS conference_by_speaker (
    speaker_id uuid,
    year int,
    name text,
    location text,
    logo text,
    url text,
    PRIMARY KEY (speaker_id, year, name)
) WITH CLUSTERING ORDER BY (year DESC, name ASC);

CREATE TABLE IF NOT EXISTS report_by_tag (
    tag_name text,
    title text,
    id uuid,
    complexity int,
    conferences set<frozen<conference_short_info>>,
    language text,
    source frozen<report_source>,
    speakers set<frozen<speaker_short_info>>,
    PRIMARY KEY (tag_name, title, id)
);

CREATE TABLE IF NOT EXISTS report_by_conference (
    conference_name text,
    year int,
    title text,
    id uuid,
    complexity int,
    language text,
    source frozen<report_source>,
    speakers set<frozen<speaker_short_info>>,
    tags set<text>,
    PRIMARY KEY (conference_name, year, title, id)
) WITH CLUSTERING ORDER BY (year DESC, title ASC, id ASC);

CREATE TABLE IF NOT EXISTS report_by_speaker (
    speaker_id uuid,
    year int,
    title text,
    id uuid,
    complexity int,
    conferences set<frozen<conference_short_info>>,
    description text,
    language text,
    source frozen<report_source>,
    tags set<text>,
    PRIMARY KEY (speaker_id, year, title, id)
) WITH CLUSTERING ORDER BY (year DESC, title ASC, id ASC);

CREATE TABLE IF NOT EXISTS report_stats_by_conference (
    conference_name text,
    year int,
    report_total counter,
    PRIMARY KEY (conference_name, year)
) WITH CLUSTERING ORDER BY (year DESC);

CREATE TABLE IF NOT EXISTS report_stats_by_speaker_for_year (
    speaker_id uuid,
    year int,
    report_total counter,
    PRIMARY KEY (speaker_id, year)
) WITH CLUSTERING ORDER BY (year DESC);

CREATE TABLE IF NOT EXISTS report_stats_by_speaker_for_conference (
    speaker_id uuid,
    conference_name text,
    report_total counter,
    PRIMARY KEY (speaker_id, conference_name)
);

CREATE TABLE IF NOT EXISTS report_stats_by_company (
    company_name text,
    year int,
    report_total counter,
    PRIMARY KEY (company_name, year)
) WITH CLUSTERING ORDER BY (year DESC);
