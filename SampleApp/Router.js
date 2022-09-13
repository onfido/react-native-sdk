import React, {Component} from 'react';
import {StyleSheet, View} from 'react-native';

import {NativeRouter, Route} from 'react-router-native';
import App from './App';
import Start from './Start';
import Finish from './Finish';

export default class Router extends Component {
  render() {
    return (
      <NativeRouter>
        <View style={styles.container}>
          <Route exact path="/" render={() => <Start />} />

          <Route
            exact
            path="/sdk/:firstName/:lastName"
            render={props => <App {...props} />}
          />

          <Route
            exact
            path="/finish/:status/:message"
            render={props => <Finish {...props} />}
          />
        </View>
      </NativeRouter>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
