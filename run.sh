#!/bin/bash

dir=$(dirname $0)
cd $dir
dir=$(pwd)

java_path=$dir/src
classes_path=$dir/out/classes
lib_path=$dir/lib

compile() {
    OLD_CLASSPATH=$CLASSPATH
    for j in $lib_path/*.jar; do
        export CLASSPATH=$CLASSPATH:$j
    done;
    echo CLASSPATH OK!

    # 所有java文件的名称存入到sources.list文件中
    rm $classes_path/sources.list
    find $java_path -name "*.java" > $classes_path/sources.list

    # 编译
    javac -encoding utf-8 -sourcepath $java_path @$classes_path/sources.list -d $classes_path
    echo javac OK!
    export CLASSPATH=$OLD_CLASSPATH
}

start() {
	if [ -f epcc.pid ]; then
		echo 系统已经启动，不能重复启动。如果确实没有重复启动，请删除epcc.pid文件后再启动
		return
	fi

	local cp=$classes_path
	for f in $lib_path/*.jar; do
		cp=$cp:$f
	done

	if [ ! -d ./logs ]; then
		mkdir ./logs
	fi

	java -server -Xmx2048M -cp $cp com.pe.PEServer >> ./logs/stdout.log 2>&1 &
	echo $! > epcc.pid
}

stop() {
	if [ ! -f epcc.pid ]; then
		return
	fi

	local pid=$(cat epcc.pid)
	echo "pid: $pid"
	kill $pid
	rm epcc.pid

	local i=0
	while [ $i -lt 65 ]; do
		i=$((i+1))
		sleep 1
		if [ ! -z "$(ps ux | grep -v grep |  grep $pid)" ]; then
			echo stopping...
		else
			break
		fi
	done

	echo Stopped.
}

echo peparser Server will be shut down!
stop
echo peparser Server shut down!

git pull
echo update OK!

compile
echo compile OK!

start
echo start OK!

