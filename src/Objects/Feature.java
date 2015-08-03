package Objects;

public final class Feature
{
    private String name;
    private String chrom;
    private String extra; // TODO: what is it for?
    private int start;
    private int end;

    public Feature(String name, String chrom, int start, int end, String extra) {
        this.name = name;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.extra = extra;
    }

    public String getChrom() {
        return chrom;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getExtra() {
        return extra;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
