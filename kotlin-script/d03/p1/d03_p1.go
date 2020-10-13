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

func buildPath(paths []string) [][]bool {
	pathMap := make([][]bool, arraySize)
	for i := 0; i < arraySize; i++ {
		pathMap[i] = make([]bool, arraySize)
	}
	x, y := arraySize/2, arraySize/2
	pathMap[x][y] = true
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
			pathMap[x][y] = true
			c++
		}
	}
	return pathMap
}

func main() {
	input, err := ioutil.ReadFile("../input.txt")
	if err == nil {
		lines := strings.Split(string(input), "\n")
		wire1 := buildPath(strings.Split(lines[0], ","))
		wire2 := buildPath(strings.Split(lines[1], ","))
		minDistance := arraySize
		for i := 0; i < arraySize; i++ {
			for j := 0; j < arraySize; j++ {
				if wire1[i][j] && wire2[i][j] && i != arraySize/2 && j != arraySize/2 {
					distance := abs(arraySize/2-i) + abs(arraySize/2-j)
					if distance < minDistance {
						minDistance = distance
						fmt.Printf("min distance = %v\n", minDistance)
					}
				}
			}
		}
	}
}
