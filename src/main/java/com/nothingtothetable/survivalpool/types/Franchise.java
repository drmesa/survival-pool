package com.nothingtothetable.survivalpool.types;

public enum Franchise
{
    CARDINALS_1898(1L, "CARDINALS_1898"),
    FALCONS_1966(2L, "FALCONS_1966"),
    RAVENS_1996(3L, "RAVENS_1996"),
    BILLS_1960(4L, "BILLS_1960"),
    PANTHERS_1995(5L, "PANTHERS_1995"),
    BEARS_1919(6L, "BEARS_1919"),
    BENGALS_1968(7L, "BENGALS_1968"),
    BROWNS_1946(8L, "BROWNS_1946"),
    COWBOYS_1960(9L, "COWBOYS_1960"),
    BRONCOS_1960(10L, "BRONCOS_1960"),
    LIONS_1930(11L, "LIONS_1930"),
    PACKERS_1919(12L, "PACKERS_1919"),
    TEXANS_2002(13L, "TEXANS_2002"),
    COLTS_1953(14L, "COLTS_1953"),
    JAGUARS_1995(15L, "JAGUARS_1995"),
    CHIEFS_1960(16L, "CHIEFS_1960"),
    RAIDERS_1960(17L, "RAIDERS_1960"),
    CHARGERS_1960(18L, "CHARGERS_1960"),
    RAMS_1936(19L, "RAMS_1936"),
    DOLPHINS_1966(20L, "DOLPHINS_1966"),
    VIKINGS_1961(21L, "VIKINGS_1961"),
    PATRIOTS_1960(22L, "PATRIOTS_1960"),
    SAINTS_1967(23L, "SAINTS_1967"),
    GIANTS_1925(24L, "GIANTS_1925"),
    JETS_1960(25L, "JETS_1960"),
    EAGLES_1933(26L, "EAGLES_1933"),
    STEELERS_1933(27L, "STEELERS_1933"),
    FORTY_NINERS_1946(28L, "FORTY_NINERS_1946"),
    SEAHAWKS_1976(29L, "SEAHAWKS_1976"),
    BUCCANEERS_1976(30L, "BUCCANEERS_1976"),
    TITANS_1960(31L, "TITANS_1960"),
    COMMANDERS_1932(32L, "COMMANDERS_1932");

    private final Long id;
    private final String code;

    Franchise(Long id, String code)
    {
        this.id = id;
        this.code = code;
    }

    public Long getId()
    {
        return id;
    }

    public String getCode()
    {
        return code;
    }

    public static Franchise fromCode(String code)
    {
        for (Franchise franchise : values())
        {
            if (franchise.code.equals(code))
            {
                return franchise;
            }
        }
        throw new IllegalArgumentException("Unknown franchise code: " + code);
    }

    public static Franchise fromId(Long id)
    {
        for (Franchise franchise : values())
        {
            if (franchise.id.equals(id))
            {
                return franchise;
            }
        }
        throw new IllegalArgumentException("Unknown franchise id: " + id);
    }
}
