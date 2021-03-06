package org.riotfamily.statistics.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.statistics.domain.CacheRegionStatsItem;

public class ClearCacheRegionCommand extends AbstractHibernateCacheCommand {

	private RiotLog log = RiotLog.get(ClearCacheRegionCommand.class);
	
	public ClearCacheRegionCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void doExecute(CommandContext context) {
		CacheRegionStatsItem crs = (CacheRegionStatsItem) context.getBean();
		clearHibernateCacheRegion(crs.getName());
	}
	
	private void clearHibernateCacheRegion(String region) {
		try {
			SecondLevelCacheStatistics stats = getSessionFactory().getStatistics().getSecondLevelCacheStatistics(region);
			Set<String> classes = getCacheEntrySet(stats);
			for (Iterator<String> iterator = classes.iterator(); iterator.hasNext();) {
				String clazz = iterator.next();
				evictCacheEntry(clazz, false);
			}
		} 
		catch (Exception e) {
			log.warn("Clearing cache region failed: " + region);
			throw new RuntimeException("Clearing cache region failed");
		}
	}
	
	private Set<String> getCacheEntrySet(SecondLevelCacheStatistics slStats) {
		Set<String> entities = Generics.newHashSet();
		
		/* Liefert z.Zt ClassCastException.
		 * (http://opensource.atlassian.com/projects/hibernate/browse/HHH-2815)
		 */
		Map<?,?> entries = (Map<?,?>) slStats.getEntries();
		
		if (slStats != null && entries != null) {
			for (Iterator<?> iterator = entries.entrySet().iterator(); iterator.hasNext();) {
				Entry<?,?> entry = (Entry<?,?>) iterator.next();
				if (entry.getValue() instanceof CacheEntry) {
					String clzz =  ((CacheEntry)entry.getValue()).getSubclass();
					if (clzz != null) {
						entities.add(clzz);
					}
				}
			}
		}
		return entities;
	}

}
