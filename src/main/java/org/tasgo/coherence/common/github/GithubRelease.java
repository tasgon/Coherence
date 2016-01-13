package org.tasgo.coherence.common.github;

import java.util.List;

public class GithubRelease {
	public class GithubUser {
		public String login, avatar_url, gravatar_id, url, html_url, followers_url, following_url, gists_url, starred_url, subscriptions_url, organizations_url,
					  repos_url, events_url, received_events_url, type;
		public int id;
		public boolean site_admin;
	}
	
	public class GithubAsset {
		public String url, name, label, content_type, state, created_at, updated_at, browser_download_url;
		public int id, size, download_count;
		public GithubUser uploader;
	}
	
	public String url, assets_url, upload_url, html_url, tag_name, target_commitish, name, created_at, published_at, tarball_url, zipball_url, body;
	public int id;
	public boolean draft, prerelease;
	public GithubUser author;
	public List<GithubAsset> assets;
}
