# run_project.R
# run with `R --slave --vanilla --file=run_project.R --args PATH_TO_DATA1 PATH_TO_DATA2...`

library(RWeka)

args=(commandArgs(TRUE))
for(i in 1:length(args)){
   # print(paste('ARG ',i,args[[i]],sep=" "))
   process(arg)
}

# make sure your clean data files are in the directory you begin in
process <- function(dsetfile) {
    # Analysis output variable (Analysis dir and Analysis extension)
    adir <- '../analysis/'
    aext <- '.txt'

    extension <- lapply(strsplit(dsetfile,'\\.'),function(x) x[2])
    name <- lapply(strsplit(dsetfile, '\\.'))

    data = ifelse(extension=='arff', read.arff(dsetfile),
            ifelse(extension=='csv', read.csv(dsetfile,
            NULL)))
    if (dset != NULL ) {
        # anything that would produce output to STDOUT (e.g. the `m` line below)
        # is instead written to the file specified by `sink()`
        sink(paste(adir, name, aext, sep = ''))

        # assuming arff as filetype
        data <- read.arff(dsetfile)
        # labels = data[14]
        # data.no.labels = data[,1:13] # for reference purposes


        # e <- evaluate_Weka_classifier(m,cost = matrix(c(0,2,1,0), ncol = 2),numFolds = 10, complexity = TRUE,seed = 123, class = TRUE)
        # Reduce
        ### PCA

        ### ICA

        ### RP

        # write.arff(data.pca, file = "data/filtered/hd_pca5.arff")

        # Cluster clean data set
        m <- SimpleKMeans(data[,1:13], control = Weka_control(N=5))
        m
        # just typing "m" will give statistics!
        data.kcluster <- data.frame(data[,1:13],kcluster = predict(m), label=data[,14])
        write.arff(data.kcluster, file = "data/hd_kcluster.arff")
        # 
        # Cluster reduced data sets

        # NNet on clean data

        # NNet on reduced data

        # NNet on clustered-reduced data
    } else {
        print('Data set must be an ARFF of CSV file.')
    }
}


