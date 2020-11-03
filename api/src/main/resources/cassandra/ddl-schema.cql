CREATE TYPE IF NOT EXISTS speaker_location (
    added_date timestamp,
    name text
);

CREATE TYPE IF NOT EXISTS speaker_company (
    added_date timestamp,
    name text
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
    locations set<frozen<speaker_location>>,
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
    name text,
    avatar text,
    bio text,
    contact_info frozen<contact_info>,
    PRIMARY KEY (id, name)
);

CREATE TABLE IF NOT EXISTS report (
    id uuid,
    title text,
    complexity int,
    conferences set<frozen<conference_short_info>>,
    description text,
    language text,
    source frozen<report_source>,
    speakers set<frozen<speaker_full_info>>,
    tags set<text>,
    PRIMARY KEY (id, title)
);

CREATE TABLE IF NOT EXISTS speaker_by_conference (
    conference_name text,
    year int,
    name text,
    avatar text,
    location frozen<speaker_location>,
    speaker_id uuid,
    PRIMARY KEY (conference_name, year, name)
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
    complexity int,
    conferences set<frozen<conference_short_info>>,
    language text,
    report_id uuid,
    source frozen<report_source>,
    speakers set<frozen<speaker_short_info>>,
    PRIMARY KEY (tag_name, title)
);

CREATE TABLE IF NOT EXISTS report_by_conference (
    conference_name text,
    year int,
    title text,
    complexity int,
    language text,
    report_id uuid,
    source frozen<report_source>,
    speakers set<frozen<speaker_short_info>>,
    tags set<text>,
    PRIMARY KEY (conference_name, year, title)
) WITH CLUSTERING ORDER BY (year DESC, title ASC);

CREATE TABLE IF NOT EXISTS report_by_speaker (
    speaker_id uuid,
    year int,
    title text,
    complexity int,
    conference frozen<conference_short_info>,
    description text,
    language text,
    source frozen<report_source>,
    tags set<text>,
    PRIMARY KEY (speaker_id, year, title)
) WITH CLUSTERING ORDER BY (year DESC, title ASC);

CREATE TABLE IF NOT EXISTS report_stats_by_conference (
    conference_name text,
    year int,
    report_total counter,
    PRIMARY KEY (conference_name, year)
) WITH CLUSTERING ORDER BY (year DESC);

CREATE TABLE IF NOT EXISTS report_stats_by_speaker (
    speaker_id uuid,
    conference_name text,
    year int,
    report_total counter,
    PRIMARY KEY (speaker_id, year, conference_name)
) WITH CLUSTERING ORDER BY (year DESC, conference_name ASC);

CREATE TABLE IF NOT EXISTS report_stats_by_company (
    company_name text,
    year int,
    report_total counter,
    PRIMARY KEY (company_name, year)
) WITH CLUSTERING ORDER BY (year DESC);
