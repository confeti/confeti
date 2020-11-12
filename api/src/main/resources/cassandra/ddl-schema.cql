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
    contact_info frozen<contact_info>,
    name text
);

CREATE TYPE IF NOT EXISTS speaker_full_info (
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

CREATE TABLE IF NOT EXISTS speaker (
    id uuid,
    avatar text,
    bio text,
    contact_info frozen<contact_info>,
    name text,
    PRIMARY KEY (id)
);

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

CREATE TABLE IF NOT EXISTS speaker_by_conference (
    conference_name text,
    year int,
    name text,
    id uuid,
    avatar text,
    location text,
    PRIMARY KEY (conference_name, year, name, id)
) WITH CLUSTERING ORDER BY (year DESC, name ASC);

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
) WITH CLUSTERING ORDER BY (year DESC, title ASC);

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
) WITH CLUSTERING ORDER BY (year DESC, title ASC);

CREATE TABLE IF NOT EXISTS report_stats_by_conference (
    conference_name text,
    year int,
    report_total counter,
    PRIMARY KEY (conference_name, year)
) WITH CLUSTERING ORDER BY (year DESC);

CREATE TABLE IF NOT EXISTS report_stats_by_speaker (
    speaker_id uuid,
    year int,
    conference_name text,
    report_total counter,
    PRIMARY KEY (speaker_id, year, conference_name)
) WITH CLUSTERING ORDER BY (year DESC, conference_name ASC);

CREATE TABLE IF NOT EXISTS report_stats_by_company (
    company_name text,
    year int,
    report_total counter,
    PRIMARY KEY (company_name, year)
) WITH CLUSTERING ORDER BY (year DESC);
