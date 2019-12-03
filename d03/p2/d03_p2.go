package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

const arraySize = 30000

func toInt(s string) int {
	i, _ := strconv.Atoi(s)
	return i
}

func abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

func buildPath(paths []string, stopAtX int, stopAtY int) ([][]int, int) {
	pathMap := make([][]int, arraySize)
	for i := 0; i < arraySize; i++ {
		pathMap[i] = make([]int, arraySize)
	}
	x, y, pathLength := arraySize/2, arraySize/2, 0
	pathMap[x][y] = 0
	for i := 0; i < len(paths); i++ {
		path := paths[i]
		direction := path[0:1]
		offset := toInt(path[1:])
		for c := 0; c < offset; {
			switch direction {
			case "R":
				x++
			case "L":
				x--
			case "U":
				y++
			case "D":
				y--
			}
			pathLength++
			pathMap[x][y] = pathLength
			c++
		}
	}
	return pathMap, pathLength
}

func main() {
	input, err := ioutil.ReadFile("../input.txt")
	if err == nil {
		lines := strings.Split(string(input), "\n")
		wire1, _ := buildPath(strings.Split(lines[0], ","), -1, -1)
		wire2, _ := buildPath(strings.Split(lines[1], ","), -1, -1)
		minSteps := -1
		for i := 0; i < arraySize; i++ {
			for j := 0; j < arraySize; j++ {
				if wire1[i][j] > 0 && wire2[i][j] > 0 && i != arraySize/2 && j != arraySize/2 {
					steps := wire1[i][j] + wire2[i][j]
					if minSteps == -1 || steps < minSteps {
						minSteps = steps
						fmt.Printf("min steps = %v\n", steps)
					}
				}
			}
		}
	}
}
