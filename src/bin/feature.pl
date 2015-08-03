#!/usr/bin/env perl

## Author: Jeong-Hyeon Choi
## Date: Feb 11 2008

## convert a gff format to a wiggle format

## Usage:
## CHECK usage with option  -h

## $Id: perl_template,v 1.2 2007-09-21 22:51:23 jeochoi Exp $


my $idDelimeter = '|';
use constant { STT=>0, STP=>1, SUM=>2, STR=>3, CNT=>4 };

use Getopt::Long qw(:config no_ignore_case);

my %cigarIndex = ('I' => 0, 'D' => 1, 'S' => 2, 'H' => 3, 'M' => 4, 'N' => 5);
my ($helpFlag, $inFile, $featFile, $outFile, $verbose, $quiet, $endLen, $norm, $log, $bblock, $sblock, $end5Flag, $end3Flag, $arc, $iScore, $missing, $idCol, @idCols, $fragment, $type, @filters, $samtools);
my $bin = 100;

GetOptions(
	"h|?|help"    => \$helpFlag,	
	"input=s"     => \$inFile,		## input file
	"output=s"    => \$outFile,		## output file
	"f|eature=s"  => \$featFile,
	"verbose+"    => \$verbose,		## verbose output
	"quiet"       => \$quiet,
	"e|end=i"     => \$endLen,
	"n|norm=i"    => \$norm,
	"log=f"       => \$log,
	"b|body=i"    => \$bblock,
	"s|stream=i"  => \$sblock,
	"total=i"     => \$total,
	"arc=f"	      => \$arc,	# use average read coverage
	"score=i"     => \$iScore,	# use average score
	"5end"        => \$end5Flag,
	"3end"        => \$end3Flag,
	"missing=s"   => \$missing,
	"columns=s"   => \$idCol,
	"l|length=s"  => \$fragment,
	"type=s"      => \$type,
	"bin=i"       => \$bin,
	"filter:s"    => \%filters,
	"samtools=s"  => \$samtools,
) || die "\n";

checkOptions();

my (%probes, @genes, $existRead, @blocks, @order);
my $numBlocks;

my (%feats, @feat, $gid, $beg, $end);
my ($totCov, $totLen) = (0, 0);
$total = getTotal($inFile) if !$total && !$arc && !defined $iScore;

#-------------------------------------------------------------------------------
print "Body=$bblock Stream=$sblock Length=$endLen";
print " Norm=$norm Total=$total" if !defined $iScore;
print " Missing=$missing Type=$type IDCol=$idCol";
print " Fragment=$fragment" if $fragment;
print " 5End=T" if $end5Flag;
print " 3End=T" if $end3Flag;
print " Score=$iScore" if defined $iScore;
print " Log=T" if $log;
print " Filter=" . join(',', sort keys %filters) if %filters;
print "\n";
#-------------------------------------------------------------------------------

print "Loading ", $featFile || 'STDIN', " ...\n" if !$quiet;
my $fin = openFile($featFile);
for (my $fno = 0; <$fin>; )
{
	next if /^#/ || /^\s*$/ || /^(chrom|chr|ref|track|browse)\b/i;
	my @a = split(/[\t\r\n]/);
	$a[1] = 0 if $a[1] < 0;
	$fno++;
	push(@{$feats{$a[0]}}, [@a, $fno]) if !exists $filters{$a[0]};
}

close($fin);


print "Processing ", $inFile || 'STDIN', " ...\n" if !$quiet;
my $rin = openInput($inFile);
$ref = '';
	load(-1);

my $out = openOutput($outFile);
	header();

	#chr3RHet	51206	51242	1
	while (1)
	{
		$ref = $order[0];
		my @old;
		if ($verbose)
		{
			print "#$ref\n";
			print "refs in start of loop: " . join(' ', keys %reads) . "\n";
			print "order in start of loop: @order\n";
		}

		foreach my $rf (@{$feats{$ref}})
		{
			@feat = @$rf;
			die "Feature is not sorted:\n@old\n@feat\n" if @old > 0 && ($old[1] > $feat[1] || ($old[1] == $feat[1] && $old[2] > $feat[2]));
			print "@feat\n" if $verbose;

			my $i = 0;
			$gid = join('', map {if (!$feat[$_]) {""} else {$feat[$_]=~s/\s//g; "$feat[$_]$idDelimeter"}} @idCols) . $feat[-1];
			($beg, $end) = @feat[1,2];
			if ($end5Flag) { if ($feat[5] eq '-') { $beg = $end; } else { $end = $beg; } }
			if ($end3Flag) { if ($feat[5] eq '-') { $end = $beg; } else { $beg = $end; } }

			makeBlock($sblock, \$i, $feat[0], $beg-$endLen, $beg        , $gid);
			makeBlock($bblock, \$i, $feat[0], $beg        , $end        , $gid) if !$end5Flag && !$end3Flag;
			makeBlock($sblock, \$i, $feat[0], $end        , $end+$endLen, $gid);

			if (!$numBlocks) { $numBlocks = $i; }
			elsif ($numBlocks != $i) { die "Different #blocks ($numBlocks $i) in $gid\n"; }
			@genes = (@feat[0, 1, 2], $feat[5]||'.');

			load(1);
			compute();
			output();
			remove();

			if ($verbose)
			{
				print "refs after remove: " . join(' ', keys %reads) . "\n";
				print "order after remove: @order\n";
			}

			%probes = ();
			@blocks = ();
			@genes = ();
			$existRead = undef;
			@old = @feat;
		}

		print "$ref done\n";
		load(0) if keys %reads <= 1;
		if ($verbose)
		{
			print "refs after loading 0: " . join(' ', keys %reads) . "\n";
			print "order after loading 0: @order\n";
		}
		delete $reads{$ref};
		shift(@order);
		map { print "@{$reads{$_}{(keys(%{$reads{$_}}))[0]}[0]}\n" } keys %reads if $verbose;

		if (keys %reads == 0)
		{
			print "Last ref: $ref\n";
			last;
		}
	}

	close($rin);
	close($out);

sub makeBlock
{
	my ($block, $rstart, $ref, $left, $right, $gid) = @_;

	my $l = ($right-$left) / $block;
	my $j = 0;
	for (my $p = $left; $j < $block; $p += $l, $j++)
	{
		my $q = $p + $l;
		$q++ if $q-$p < 1;
		for (my $b = int($p/$bin); $b <= int($q/$bin); $b++)
		{
			push(@{$probes{$b}}, [int($p), int($q), $$rstart]);
		}
		#push(@{$probes{int($q/$bin)}}, [int($p), int($q), $$rstart]) if int($p/$bin) != int($q/$bin);
		@{$blocks[$$rstart++]}[STT,STP] = (int($p), int($q));
		#print "block: $$rstart $p $q\n" if $verbose;
	}
}

sub strand
{
	return $_[0] & 0x10 ? 1 : 0;
}

# 8M1D46M
sub parseCigar
{
	my ($cigar) = @_;
	my @cigars = (0, 0, 0, 0, 0, 0);

	while ($cigar =~ /(\d+)([IDSHMN])/ig) { $cigars[$cigarIndex{$2}] += $1; }

	return @cigars;
}

sub load
{
	my ($flag) = @_;
	# 1 : read while validate
	# 0 : read while the same ref
	# -1 : read one line

	while (<$rin>)
	{
		next if /^#/ || /^\s*$/ || /^chrom|ref|track|browse/i || /^chr\b/;

		my @a = split(/[\t\r\n]/);

		if ($type ne 'bed')
		{
			next if $a[2] eq '*' || exists $filters{$a[2]};
			my @cigars = parseCigar($a[5]);
			@a[0..5] = ($a[2], $a[3]-1, $a[3]-1+$cigars[1]+$cigars[4], $a[0], '.', strand(int($a[1])) ? '-' : '+');
		}
		else
		{
			next if exists $filters{$a[0]};
		}

		if ($fragment)
		{
			@a[1,2] = $a[5] eq '-' ? ($a[2]-$fragment, $a[2]) : ($a[1], $a[1]+$fragment);
		}

		my $last = $flag == -1 ? 1 : $a[0] ne $ref;
		$last ||= $a[1] >= $feat[2]+$endLen if $flag == 1;

		push(@order, $a[0]) if !@order || $order[-1] ne $a[0];
		print "\tload: ", join("\t", @a, int($a[1]/$bin)..int($a[2]/$bin)) if $verbose;

		if ($last || ($flag == 1 && $a[2] >= $feat[1]-$endLen))
		{
			foreach my $b (int($a[1]/$bin)..int($a[2]/$bin))
			{
				push(@{$reads{$a[0]}{$b}}, \@a);
			}
			print "  ->  ________" if $verbose;
		}
		else
		{
			print "  ->  discarded" if $verbose;
		}

		print "\n" if $verbose;

		last if $last;
	}
}

sub remove
{
	foreach my $b (sort {$a<=>$b} keys %{$reads{$ref}})
	{
		if ($b < int(($feat[1]-$endLen)/$bin))
		{
			print "\tremove: block $b\n" if $verbose;
			map {print "\t\t@$_\n"} @{$reads{$ref}{$b}} if $verbose;
			delete $reads{$ref}{$b};
		}
	}
}

sub compute
{
	my %done;

	foreach my $b ($verbose ?sort {$a<=>$b} keys %probes : keys %probes)
	{
		print "B: $b\n" if $verbose;
		foreach my $f (@{$probes{$b}})
		{
			print "\tF:$b @$f\n" if $verbose;
			next if !exists $reads{$ref}{$b};
			foreach my $ra (@{$reads{$ref}{$b}})
			{
				print "\t\tR:@$ra\n" if $verbose > 1;
				next if exists $done{$f->[2]}{$ra};
				my $mid = int(($ra->[1]+$ra->[2]-1)/2+0.5);
				my ($fbeg, $fend) = $f->[0] == $f-> [1] ? ($f->[0], $f->[1]+1) : ($f->[0], $f->[1]);
				if ($ra->[2] > $fbeg && $fend > $ra->[1])
				{
					my $len = _min($ra->[2], $fend) - _max($ra->[1], $fbeg);
					add($f->[2], defined $iScore ? $ra->[$iScore] : $len);
					$done{$f->[2]}{$ra} = 1;
					print "\t\t(@$f) ", $blocks[$f->[2]][CNT]||0, $blocks[$f->[2]][SUM]||0, "  [$len] @$ra $ra\n" if $verbose;
				}
			}
		}
	}
}

sub add
{
	my ($bno, $cov) = @_;
	$blocks[$bno][SUM] += $cov;
	$blocks[$bno][CNT] ++ if defined $iScore;
	$existRead++;
}

sub output
{
	return if !$existRead;
	print $out join("\t", $gid, @genes);
	my $j = 0;
	foreach my $b ($genes[STR] eq '-' ? reverse @blocks : @blocks)
	{
		if ($b->[SUM])
		{
			printf $out "\t%.3f", $arc ? $b->[SUM]/($b->[STP]-$b->[STT])/$arc
			                           : defined $iScore ? $b->[SUM]/$b->[CNT]
												                  : $log ? log($log+($b->[SUM]*($norm/$total)/($b->[STP]-$b->[STT])))/log(2)
												                         : $b->[SUM]*($norm/$total)/($b->[STP]-$b->[STT]);
		}
		else
		{
			print $out "\t", $missing;
		}
	}
	print $out "\n";
}

sub header
{
	my (@a, $i, $j);
	for ($i = $sblock; $i > 0; $i--) { $a[$i-1] = '-' . int($endLen/$sblock*($sblock-$i+1)); }

	if ($end5Flag || $end3Flag)
	{
#		$a[$sblock] = 0; $i++; $j = 1; # generates -10 +20
#		$a[$sblock] = 0; $i++; $j = 0; # generates 0 +20
		$j = 0;                        # generates -10 +10
	}
	else
	{
#     generates 0% 0.02% 0.04% ... 99.6% 100%
#		for ($i = 0; $i < $bblock; $i++) { $a[$i+$sblock] = (100*$i/$bblock) . '%'; }
#     generates 0% 0.0.200400801603206% 0.400801603206413% ... 99.7995991983968% 100%
		for ($i = 0; $i < $bblock; $i++) { $a[$i+$sblock] = sprintf('%.3f%%', 100*$i/($bblock-1)); }
		$a[$i+$sblock-1] = '100%';
		$j = 0;
	}
	for ( ; $j < $sblock; $i++,$j++) { $a[$i+$sblock] = '+' . int($endLen/$sblock*($j+1)); }
	print $out join("\t", "#Key\tRef\tStart\tEnd\tStrand", @a), "\n";
}

sub getTotal
{
	my ($fileName) = @_;

	my $in = openInput($fileName);
	my $i;

	for ($i = 0; <$in>; $i++)
	{
		next if /^#/ || /^\s*$/;
		my @a = split;
	}

	close($in) if defined $fileName;
	return $i;
}

#-------------------------------------------------------------------------------

sub openInput
{
	return openFile($type eq 'bed' ? $inFile : ("$samtools view" . ($type eq 'sam' ? ' -S' : '') . " -F 4 -F 512 -F 1024 $inFile |"));
}

sub openFile
{
	my ($fileName) = @_;

	return *STDIN unless defined $fileName;

	my ($fd);
	open($fd, $fileName =~ /.gz$/ ? "gzip -dc $fileName |" : $fileName) || die("Open error: $fileName");
	return $fd;
}

sub openOutput
{
	my ($fileName) = @_;

	return *STDOUT unless defined $fileName;

	my ($fd);
	open($fd, $fileName =~ /.gz$/ ? "| gzip -c > $fileName" : ">$fileName") || die("Open error: $fileName");
	return $fd;
}

sub _parseRangeArg1
{
	my @array = _parseRangeArg(@_);
	_decrease(\@array, 1);
	return @array;
}

sub _parseRangeArg
{
	my %ret;

	foreach my $arg (@_)
	{
		my @a;

		foreach my $r (split(/,/, $arg))
		{
			push(@a, ($r =~ /(\d+)-(\d+)/) ? $1..$2 : $r);
		}

		map {$ret{$_}++} @a;
	}

	return sort {$a<=>$b} keys %ret;
}

sub _decrease
{
	my ($array, $value) = @_;
	map {$_-=$value} @$array;
}

sub _min(@)
{
	return $_[_minIndex(@_)];
}

sub _minIndex(@)
{
	my (@array) = @_;
	my $min = 0;

	for (my $i = 1; $i < @array; $i++)
	{
		$min = $i if $array[$min] > $array[$i];
	}

	return $min;
}

sub _max(@)
{
	return $_[_maxIndex(@_)];
}

sub _maxIndex(@)
{
	my (@array) = @_;
	my $max = 0;

	for (my $i = 1; $i < @array; $i++)
	{
		$max = $i if $array[$max] < $array[$i];
	}

	return $max;
}

sub checkOptions
{
	$inFile  = shift(@ARGV) if !defined $inFile  && @ARGV > 0;
	$outFile = shift(@ARGV) if !defined $outFile && @ARGV > 0;
	die "The output file name is the same as the input file name\n" if defined $inFile && defined $outFile && $inFile eq $outFile;

	if ($helpFlag || !defined $endLen || !$inFile || !$featFile || !$bblock || !$sblock || !$type)
	{
		die("Arguments: [options] -e len -s stream -b body -f feature_file -type <bed|sam|bam>\n"
		  . "\t-f       file      feature file\n"
		  . "\t-i       file      input file\n"
		  . "\t-t       string    input type: BED, SAM, or BAM\n"
		  . "\t-e       int       the length of upstream and downstream\n"
		  . "\t-b       int       the number of blocks for body\n"
		  . "\t-s       int       the number of blocks for upstream and downstream\n"
		  . "\t-o       file      output file\n"
		  . "\t--- Optional paramerts -----------------------------------------------------------\n"
		  . "\t-c       int       comma-separated numbers for ID columns                  [4,5]\n"
		  . "\t-l       int       specify the fragment size to lengthen reads to the 3' end\n"
		  . "\t-s       int       use score column instead of read coverage for BS-seq and Infinium\n"
		  . "\t-n       int       normalization value                                     [1000000]\n"
		  . "\t-t       int       total number of reads\n"
		  . "\t-5                 compute tag density only around the 5' end\n"
		  . "\t-3                 compute tag density only around the 3' end\n"
		  . "\t-log     float     log tranform after adding the specified value\n"
		  . "\t-filter  int       specify filtered reference IDs\n"
		  );
	}

	die "File type should be either bed, sam, or bam\n" if ($type !~ /^(bed|sam|bam)$/);

	$norm = 1000000 if !$norm;
	$verbose = 0 if !$verbose;
	$missing = 0 if !$missing;
	$iScore-- if $iScore;
	@idCols = _parseRangeArg1($idCol || '4,5');
        $samtools = 'samtools' if !$samtools;
}
